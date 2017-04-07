require 'asciidoctor'
require 'asciidoctor-pdf'
require 'asciidoctor/extensions'

format = ARGV[0] || 'pdf'

Asciidoctor::Extensions.register do
  # Converts page breaks (`<<<`) into unnamed section titles (`== !`)
  # FIXME this misses evaluating attribute entries
  preprocessor do
    process do |doc, reader|
      prev_line = nil
      Asciidoctor::Reader.new reader.readlines.map {|line|
        prev_line_blank = prev_line.nil_or_empty?
        prev_line = line
        (prev_line_blank && line == '<<<') ? '== !' : line
      }
    end
  end

  # creates document structure for handout (slide image + notes, each on its own page)
  treeprocessor do
    def create_no_notes_block doc, context = :open
      block = Asciidoctor::Block.new doc, context, content_model: :compound
      block << (Asciidoctor::Block.new block, :paragraph, source: ['_No notes._'], subs: :default)
      block
    end

    def create_slide_image_block doc, slidenum, exportdir = '.'
      Asciidoctor::Block.new doc, :image, source: nil, attributes: { 'target' => %(#{exportdir}/slide-#{slidenum.to_s.rjust 3, '0'}.png), 'pdf-width' => '100%' }
    end

    def create_page_break_block doc
      Asciidoctor::Block.new doc, :page_break
    end

    process do |doc|
      notes_context = doc.backend == 'pdf' ? :open : :sidebar
      slides_exportdir = doc.attr 'slides-exportdir', '.'
      notes = (doc.find_by context: :section).select {|sect| sect.level == 1 }.map {|sect|
        if (notes_for_sect = (sect.find_by context: :open, role: 'notes'))
          notes_for_sect[0].tap {|block|
            block.context = notes_context unless notes_context == :open
            block.parent = doc
          }
        else
          create_no_notes_block doc, notes_context
        end
      }

      notes_by_page = []
      # temporary title page
      notes_by_page << (create_slide_image_block doc, 1, slides_exportdir)
      notes_by_page << (create_no_notes_block doc, notes_context)

      notes.each_with_index {|notes_block, idx|
        unless idx == 0
          notes_by_page << (create_page_break_block doc)
        end
      
        notes_by_page << (create_slide_image_block doc, (idx + 2), slides_exportdir)
        notes_by_page << notes_block
      }

      # replace AST with collection of notes
      doc.blocks.replace notes_by_page
      nil
    end
  end
end

Asciidoctor.convert_file '../src/slides.adoc',
    backend: format,
    doctype: 'book',
    to_file: %(handout.#{format}),
    header_footer: true,
    safe: :unsafe,
    attributes: 'slides-exportdir=../build/asciidoc/deckjs noheader nofooter icons=font pagenums pdf-style=datastax-handout-theme.yml pdf-fontsdir=fonts'

=begin
def layout_cover_page pos, doc
  return unless pos == :front
  on_page_create do
    # FIXME use slide_number instead of page_number in case notes run over
    bg_image = %(mapping-patterns_#{page_number}.png)
    canvas { image bg_image, position: :center, fit: [bounds.width, bounds.height] }
  end
end

doc.converter.class.send :define_method, :layout_cover_page, (method :layout_cover_page)
=end
