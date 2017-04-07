## The Presentation Framework Itself

The project in this directory builds a [Bespoke.js](http://markdalgleish.com/projects/bespoke.js/)-based presentation script. You can rebuild the script by following these steps:

* [Install npm](https://nodejs.org/en/) (npm is a part of node.js)
* [Install Gulp](https://github.com/gulpjs/gulp/blob/master/docs/getting-started.md)
* Install the dependencies by running `npm install`
* Build the code by running `gulp`

As a user of the curriculum project, you never _need_ to build the framework. But if you ever want to make changes to the styles, the template, or add a Bespoke plugin to expand presentation functionality, this is where you do that.

The rest of this README explains the important parts of the framework directory.


### `backend/`
The Slim templates used by the Asciidoctor plugin to convert sources into HTML. These templates create markup ready to be used by Bespoke.js.

### `styles/`

The LESS stylesheets used by the Slim templates. Also various images and fonts needed by the final HTML output.

### `scripts/`

Contains the main JavaScript source file that calls Bespoke.js, converting the HTML file containing the script into an in-browser presentation. Gulp operates on this source, collecting dependencies and creating a minified output. `scripts/src/main.js` is the input to the build, and `/scripts/dist/curriculum.js` is the output. The output file is tracked by Git (contrary to the usual practice), because it is otherwise painful to have the curriculum Gradle build run the Bespoke Gulp build, and the Bespoke framework changes much less often than the curriculum content.


### `bespoke-animation/`

A custom Bespoke.js plugin that adapts step-by-step animations to the Bespoke slide API. Automatically incorporated by the Gulp build. Normally Bespoke plugins come from NPM, but this plug is nonstandard and tracked locally in this repo.

### `bespoke-onstage/`

A custom Bespoke.js plugin providing a presenter preview mode. Normally Bespoke plugins come from NPM, but this plug is nonstandard and tracked locally in this repo.

### `presenter-mode/`

The HTML and CSS used by the `bespoke-onstage` plugin. The Gradle curriculum plugin copies these files to the curriculum `build/` directory when building a course or a vertex.

### `guplfile.js`

The Gulp build file that assembles Bespoke and its various dependencies into `scripts/dist/curriculum.js`. You can build the framework just by running `gulp`, or do interactive development on stylesheets and templates by running `gulp watch`.
