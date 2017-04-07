'use strict';

var SVG = require('svg.js');

window.Animation = (function () {

  var canvasWidth = 1200;
  var canvasHeight = 600;
  var animationsByNodeId = {};

  function registerAnimatedCanvas(canvasId, initAnimatedCanvas) {

    var canvas = SVG(canvasId).viewbox(0, 0, canvasWidth, canvasHeight);

    // Make our animated canvas "searchable" in the DOM
    canvas.node.classList.add('animated-canvas');

    var imageList = [];
    var imageStates = [];
    var currentStep = 0;
    var initDone = false;

    function insertImage(filename, width, height, x, y, opacity) {

      opacity = (opacity != null) ? opacity : 1;

      if (initDone) {
        console.log('you CANNOT insert images after init');
        return;
      }

      var image = canvas.image(filename, width, height)
        .move(x, y)
        .opacity(opacity);

      imageList.push(image);

      return image;
    }

    function group(members) {
      if (arguments.length > 1) {
        members = [].concat.apply([], arguments);
      }
      return canvas.set(members);
    }

    var stepFunctions = initAnimatedCanvas(insertImage, group);

    initDone = true;

    animationsByNodeId[canvas.node.id] = {
      canvas: canvas,
      imageList: imageList,
      imageStates: imageStates,
      currentStep: currentStep,
      stepFunctions: stepFunctions,
    };
  }

  function getAnimation(slide) {
    var canvasNode = slide.querySelector('.animated-canvas');
    if (canvasNode != null) {
      return animationsByNodeId[canvasNode.id]
    }
  }

  function finishOngoingAnimations(animations) {
    if (Array.isArray(animations)) {
      animations.forEach(function (anim) {
        // this stops the animation
        // and forces the elements to be in their final state
        anim.finish();
      });
    }
  }

  function next(slide) {

    var animation = getAnimation(slide);
    if (animation == null) {
      return;
    }

    finishOngoingAnimations(animation.currentAnims);

    // record all images attr before animating
    var state = animation.imageList.map(function (image) {
      return {
        image: image,
        attr: image.attr(),
      };
    });
    animation.imageStates[animation.currentStep] = state;

    if (animation.currentStep >= animation.stepFunctions.length) {
      return;
    }

    var innerAnims = animation.stepFunctions[animation.currentStep]();

    // record ongoing animations
    if (innerAnims == null) {
      innerAnims = [];
    }
    if (innerAnims != null && !Array.isArray(innerAnims)) {
      innerAnims = [innerAnims];
    }
    animation.currentAnims = innerAnims;
    animation.currentStep += 1;

    return false;
  }

  function prev(slide) {

    var animation = getAnimation(slide);
    if (animation == null || animation.currentStep <= 0) {
      return;
    }

    finishOngoingAnimations(animation.currentAnims);

    animation.currentStep -= 1;

    // rollback all images attr to previous state
    animation.imageStates[animation.currentStep].forEach(function (imgAttr) {
      imgAttr.image.attr(imgAttr.attr);
    });

    return false;
  }

  return {
    registerAnimatedCanvas: registerAnimatedCanvas,
    next: next,
    prev: prev,

    // legacy
    registerCanvas: function () {
      console.error('legacy call registerCanvas');
    },
    registerSVG: function () {
      console.error('legacy call registerSVG');
    },
    addAnimation: function () {
      console.error('legacy call addAnimation');
    },
    animate: function () {
      console.error('legacy call animate');
    },
    goToNextStep: function () {
      console.error('legacy call goToNextStep');
    },
    goToPreviousStep: function () {
      console.error('legacy call goToPreviousStep');
    },
    resetSteps: function () {
      console.error('legacy call resetSteps');
    },
  };
})();

module.exports = function () {

  return function (deck) {

    deck.on('next', function (e) {
      return Animation.next(e.slide);
    });

    deck.on('prev', function (e) {
      return Animation.prev(e.slide);
    });
  };
};
