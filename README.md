# DataStax Curriculum Plugin

This Gradle plugin builds curriculum from Asciidoc sources organized according to the DataStax knowledge graph system. 

## Examples for the comically impatient

All of this README is important, but if you can't stand waiting and want to see an example build file before you even know the difference between a course and a node, here you go.

### Course build file

```groovy
plugins {
  id 'com.datastax.gradle.curriculum.plugin' version '0.2'
}

apply plugin: 'com.datastax.gradle.curriculum.plugin'

courseResources {
  definition = file('how-to-curriculum.course')
}
```

### Knowledge Graph Node build file

```groovy
plugins {
  id 'com.datastax.gradle.curriculum.plugin' version '0.2'
}

apply plugin: 'com.datastax.gradle.curriculum.plugin'
```

## Building the plugin

You should be able to use the plugin from Gradle plugin repository without building it, but of course we value your experimentation and contributions, so we want you to know how to build.

* If you're going to be changing anything, you should fork the project. See the `CONTRIBUTING.md` file for the requested workflow.
* `gradlew install` from the project root will build the plugin and install it in your `~/.m2` directory.
* In the `build.gradle` file, change the `def versionNumber = 'xxx'` assignment to the new version number you'd like to use. For local builds, we recommend adding the `-SNAPSHOT` suffix to your version number (e.g., `0.2.4-SNAPSHOT`). Be sure to remove this suffix before submitting a pull request.

## Important tasks

* `clean`: removes the `build` directory, which contains all build output.
* `vertex`: creates a slide deck for a single knowledge graph node.
* `watch`: watches the files of a single knowledge graph node and rebuilds the curriculum if any file changes. NOTE: does not work on courses.
* `course`: assembles all of the nodes in the course description files into production-ready course output. The `build/index.html` file is the entry point to the course.
* `pdf`: creates a PDF of all course slides plus speaker notes. NOTE: this takes a while to run and needs a large JVM heap. The plugin sets the JVM heap to something that should be large enough, but mileage always varies.
* `server`: runs an embedded Jetty server in the build directory, allowing you to view your curriculum at http://localhost:8080.
* `bundle`: zips up everything that `course` creates into a single distributable archive.

## Curriculum architecture

This plugin assumes it will be operating on a repository full of Asciidoc-based curriculum. The plugin is quite opinionated, specifying not only certain file names and directory conventions, but a fairly rich modular curriculum model.

### The Knowledge Graph

In this model, curriculum is composed of a tree of concepts organized as a mind map. We call this mind map the _knowledge graph_. Here is a hypothetical knowledge graph for content about the DataStax Curriculum system:

![image](https://cloud.githubusercontent.com/assets/63223/17630012/be45ad36-607a-11e6-869a-98bbc149728e.png)

This knowledge graph is isomorphic to a directory tree, whose root directory is the root of the curriculum project itself. Each node in the graph is an individual packet of curriculum, including slides, notes, exercises, and solutions. Internally, we refer to knowledge graph nodes as _vertices_. Vertices can be leaf nodes on the tree or any other node that is not the root. For this knowledge graph, we would have a directory tree that looks like this:

```
.
├── animation
│   ├── api-tutorial
│   ├── svg-js
│   └── svg-tips
├── architecture
│   ├── course-file-structure
│   ├── knowledge-graph
│   ├── node-file-structure
│   └── tips-and-tricks
├── delivery
│   ├── classroom-techniques
│   │   ├── adult-learning-principles
│   │   ├── learning-objectives
│   │   └── using-a-parking-lot
│   └── teaching-from-browser
│       ├── key-commands
│       ├── navigating-a-course
│       └── speakers-notes
└── design-principles
    ├── animation
    ├── images
    ├── printed-output
    └── text
```

Each of those nodes can be built and used individually. (See the description of the `vertex` task for details.) However, most interesting content is composed of many nodes taught in a particular order. We'll look at that next.

### The Course Model

A _course_ is an ordered list of nodes. In data structure terms, it is the list formed by traversing the knowledge graph in the sequence used when teaching live in the classroom. 

Because courses can be complex and cover multiple days of instruction, we also include a concept of _modules_. A module is an ordered list of nodes plus a module name. (With modules in place, we can think of a course as an ordered list of modules.)

In this example, we're creating a course called _How To Curriculum_, which contains two modules, each of which contains multiple nodes. The outline of our course looks something like this:

* How to Curriculum
  * Introduction
    * Getting to know each other
    * Course learning objectives
  * The DataStax curriculum system
    * The data model
    * Node file format
    * Using the browser

This is a sensible outline, but now we are faced with the problem of mapping it onto our knowledge graph.

The _DataStax curriculum system_ module seems easy enough. It would map onto node paths something like this:

* The data model: `architecture/knowledge-graph`
* The file format: `architecture/node-file-structure`
* Using the browser: `delivery/teaching-from-browser/key-commands`

But the _Introduction_ module doesn't map onto our knowledge graph in any obvious way. Its two nodes contain material that only makes sense in the context of this particular course, but don't otherwise belong in our knowledge domain. This is a common problem when creating courses in this system. The solution is to introduce new nodes _at the course level_. For now, you can think of this as a knowledge graph namespace beginning with the path `courses/[course-name]`. In our example, the first course-specific vertex would be called `courses/how-to-curriculum/getting-to-know-each-other`. We discuss file structure below, but for now it is enough to know that these course-specific nodes are located underneath the course's root directory, entirely apart from the rest of the knowledge graph proper.


### Curriculum directory structure

### Node file structure

A curriculum node is a self-contained packet of instructional materials including an outline, slides, exercises, exercise solutions, and learning objectives. We'll describe each of the components of a node in detail.

```
.
└── animation
    ├── build.gradle
    ├── assets
    │   └── <PSD/Graffle/Sketch/Visio files>
    ├── images
    │   └── <PNG/JPG/SVG files>
    ├── js
    │   └── animations.js
    └── src
        ├── slides.adoc
        ├── includes.adoc
        ├── exercises.adoc
        ├── solutions.adoc
        ├── outline.adoc
        ├── objectives.adoc
        └── quiz.adoc
```

* `build.gradle`. This is the Gradle build file to build the node by itself. Most curriculum development occurs at the node level, with nodes being combined into courses after they are fully developed. This build file is really boilerplate, probably looking like this (although the plugin version may vary):

```groovy
plugins {
  id 'com.datastax.gradle.curriculum.plugin' version '0.2'
}

apply plugin: 'com.datastax.gradle.curriculum.plugin'
```

* `assets`. Assets are binary files used to create node graphics, but are not themselves production images. Usually these are PSD, Sketch, OmniGraffle, or Visio files. Since asset files can be large and are not usually terribly Git-friendly, something like [GitHub LFS](https://git-lfs.github.com) might come in handy to help manage them.

* `images`. This directory contains the production images referenced by the slides, exercises, and other Asciidoc sources. We recommend SVG for all images, but PNG and JPG work just fine too.

* `js`. This optional directory usually contains just one file: `animation.js`. This file contains JavaScript code that describes the animation steps in the slides.

* `src`. This directory contains the Asciidoc sources that form the node's curriculum. Each component is described below:
    * `slides.adoc`. This is a template file that allows a node's slides to be build locally, apart from a course. It should always look like this:

```Asciidoc
= This is the Curriculum Node Title
:backend: deckjs
:deckjs_theme: datastax
:deckjs_transition: fade
:navigation:
:status:
:notes:
:split:
:animation:
:customjs: js/animation.js
:icons: font


:slide_path: slides
:image_path: images
include::includes.adoc[]
```

** `includes.adoc`. The actual slide content that will be rendered into bespoke.js-compatible HTML for display to students. Slides begin with an H2 title (e.g., `== Slide title`) followed by content. Some authors like to break slides into sections within a node (e.g., challenge, knowledge, solution, etc.) and include them using the `include::` macro inside of the `includes.adoc` file. This structure is up to you.
** `exercises.adoc`. The hands-on exercises that accompany the node. Exercises are rendered as HTML for display in the browser.
** `solutions.adoc`. The solutions to the exercises. Also rendered as HTML.
** `outline.adoc`. An optional outline for the node. Since nodes are intended to be small--perhaps five or ten minutes of lecture--the outline should not be complex.
** `objectives.adoc`. An optional list of the node's learning objectives. A good curriculum engineering methodology begins by defining achievable, learner-centered, verifiable objectives.
** `quiz.adoc`. An optional set of assessment questions. Ideally the questions will verify that the learning objectives have been met. These are multiple-choice or true/false (sometimes called "objective") questions. Here is an example quiz file:
    
```asciidoc
== Gremlin Path Traversal

[.question]

image::{image_path}/small-graph-schema.svg[]

What will the following traversal return?

`g.V().has("person","name","Arnold Schwarzenegger").both().both().path().by(label).dedup()`

[.answers]
[upperalpha]
A. first possible answer
B. second possible answer
C. third possible answer
D. fourth possible answer

[.correct]
* B

== Storage Options

[.question]
Cassandra works best on network attached storage.

[.correct]
FALSE

```
    

### Course file structure

Each course has its own root directory under the `/courses` path. The parts of a course are described below.


```asciidoc
.
├── knowledge graph content...
└── courses
    └── how-to-curriculum
        ├── build.gradle
        ├── how-to-curriculum.course
        └── src
           ├── .gitignore
           └── index.adoc
```

* An `index.adoc` file. You are free to customize this file to your heart's content, but here's a sensible beginning:

```
= Really Compelling Course Name
:backend: html5

== Modules
include::module-list.adoc[]

== Exercises
include::exercise-list.adoc[]
```

* A course description file

* A `.gitignore` file with the following entries:

```
slides*.adoc
solution-list.adoc
exercise-list.adoc
module-list.adoc
```

### Course description file format

TODO

### Where to put big things

Often in the production of curriculum, certain large assets might pop up, like screencast videos or Photoshop files. There is no possible world in which files like this belong in a Git repository

TODO

## Benefits and drawbacks

TODO

### Cool things about this system

* Everything is text, so it lives in Git just fine.
* Because it lives in Git, you can form remote teams around curriculum engineering and use the same collaboration techniques that developers have been using for the past twenty years.
* It's modular, so you can build up a body of curriculum on large and complex subject matter, and easily mix and match nodes into new courses as needed.
* When you update curriculum in a single node, all courses using that node get the update on their next build.
* If you need to keep parallel versions of curriculum in production, you can use time-test Git release branching methods to do so.
* The output is web-native, so it's easy to publish online.

### Less-than-cool things

* Animation is a pretty big pain. It can do things no PPT or Keynote deck can even contemplate, but you have to write JavaScript code to get there.
* There is no graphical editor. (Yet. Seems like we are one ambitious Atom plugin away from victory here...)
* You have to wrangle browsers a tiny bit to teach in a classroom. Opening a keynote file and hitting `Cmd-Option-P` is admittedly fewer steps, but this is just a setup cost.



## Future Plans

We need a few things going forward:

* Live preview. It seems like the right Atom plugin could make this happen.
* Dual display support. This might also need to take place inside of Atom, but the right JavaScript hacks could also do it. The goal is to press a single key to open a browser window on the projector display and turn the laptop display into a full-screen prsenter view.
* An animation DSL to replace the current JavaScript API. We'll always want access to JavaScript, but easy things should be easy. This feature should probaly live in Asciidoctor.
* 
