setup = {
  js '''$("*:not(section).slide").removeClass("slide deck-after");
// reinitialize deck.js w/o fragments, but trap exceptions to avoid stalling deck2pdf
try { $.deck("section.slide"); } catch (e) { console.log(e); }
$(".deck-header, footer, .deck-status").each(function() { $(this).hide(); });
$("html").removeClass("csstransitions");
$("body").css("background", "none");
$("body, .deck-container").css("overflow-y", "hidden");
var overlay = document.createElement("div");
overlay.setAttribute("style", "position: absolute; border: 1px solid #b6bbb7; height: 100%; width: 100%; top: 0; z-index: 10000; box-sizing: border-box");
document.body.appendChild(overlay);'''
}

nextSlide = {
  js '$.deck("next")'
}

totalSlides = {
  js '$.deck("getSlides").length'
}

// 150 may be sufficient, but 200 seems like a sure thing
pause = 200
