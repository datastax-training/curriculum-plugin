'use strict';

var bespoke = require('bespoke');
var classes = require('bespoke-classes');
var nav = require('bespoke-nav');
var scale = require('bespoke-scale');
var animation = require('bespoke-animation');
var bullets = require('bespoke-bullets');
var hash = require('bespoke-hash');
var prism = require('bespoke-prism');
var progress = require('bespoke-progress');
var onstage = require('bespoke-onstage');

// Bespoke.js
bespoke.from({ parent: 'article.deck', slides: 'section' }, [
  classes(),
  nav(),
  scale(),
  animation(),
  bullets('.build, .build-items > *:not(.build-items)'),
  hash(),
  prism(),
  progress(),
  onstage(),

  //overview({ columns: 5 }),
  // bullets('.build ul li'),
  // ...or fuse the .build-items:not(.build) list with first item
  //bullets([
  //  '.build',
  //  '.build-items.build > *:not(.build-items)',
  //  '.build-items:not(.build)',
  //  '.build-items:not(.build) > *:not(.build-items):not(:first-child)'].join(', ')
  //),

  // function(deck) {
  //   window.addEventListener('load', function fitObjects() {
  //     // TODO also honor "fill" which will force the image to fill
  //     Array.prototype.forEach.call(deck.parent.querySelectorAll('figure.image.max-fill'), function (figure) {
  //       var img = figure.querySelector('img');
  //       // non-flexbox approach:
  //       var slide = figure.parentNode;
  //       img.style.maxHeight = (slide.offsetHeight - figure.offsetTop - parseInt(getComputedStyle(slide).paddingBottom, 10)) + 'px';
  //       // flexbox approach:
  //       //figure.parentNode.style.display = 'flex';
  //       //figure.style.overflow = 'hidden';
  //       //figure.style.flex = '1';
  //       //var img = figure.querySelector('img');
  //       //img.style.maxHeight = figure.offsetHeight + 'px';
  //       //figure.style.overflow = '';
  //       //figure.style.flex = '';
  //       //figure.parentNode.style.display = '';
  //     });
  //     window.removeEventListener('load', fitObjects);
  //   }, false);
  // }
]);
