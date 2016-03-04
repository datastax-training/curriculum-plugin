Animation.animate(function () {
  registerCanvas('drawing');
  var node1x = 547;
  var node1y = 60;
  var ring1 = registerSVG('drawing', '${image_path}/ring1.svg', 388, 388, 400, 70);
  var ring2 = registerSVG('drawing', '${image_path}/ring2.svg', 388, 388, 400, 70);
  var node1 = registerSVG('drawing', '${image_path}/node1.svg', 90, 90, node1x, node1y);
  var node2 = registerSVG('drawing', '${image_path}/node2.svg', 90, 90, 715, 225);
  var node3 = registerSVG('drawing', '${image_path}/node3.svg', 90, 90, 550, 385);
  var node4 = registerSVG('drawing', '${image_path}/node4.svg', 90, 90, -100, 225);

  var datums = [];
  var halves = [];
  var blueDatums = [];

  function fillNode(nodex, nodey, datumFileName, addToHalves)
  {
    var pi = 3.1415927;
    var twoPi = 2 * pi;
    for(numCircles = 1; numCircles <= 6; numCircles++)
    {
      var numDatumsThisCircle = numCircles * 6;
      var radius = numCircles * 6;
      for(i = 0; i < numDatumsThisCircle; i++)
      {
        alternate = ! addToHalves;
        if(alternate)
          i += 1;
        var theta = i * (twoPi / numDatumsThisCircle)
        var datum = registerSVG('drawing', datumFileName, 8, 8, nodex + 41 + radius * Math.cos(theta), nodey + 40 + radius * Math.sin(theta));
        datum.image.opacity(0);
        datums.push(datum);
        if(addToHalves && (numCircles + i) % 2 == 0)
          halves.push(datum);
      }
    }
  }
  fillNode(node1x, node1y, '${image_path}/datum-yellow.svg', true);
  fillNode(715, 225, '${image_path}/datum-blue.svg', false); // node2
  fillNode(550, 385, '${image_path}/datum-bright-green.svg', false); // node3
  var all = [ring2];
  all.forEach(function(i){ i.image.opacity(0); });
  addAnimation('drawing', function() {
    datums.forEach(function(d){ d.image.animate().opacity(1);});
  });
  addAnimation('drawing', function() {
    node4.image.animate().move(300, 225);
  });
  addAnimation('drawing', function() {
    node4.image.animate().move(390, 225);
  });
  addAnimation('drawing', function() {
    ring2.image.animate().opacity(1);
  });
  addAnimation('drawing', function() {
    halves.forEach(function(datum) {
      var newDatum = registerSVG('drawing', '${image_path}/datum-green.svg', 8, 8, datum.image.x(), datum.image.y());
      newDatum.image.opacity(0);
      datum.image.animate().opacity(0);
      newDatum.image.animate().opacity(1);
      blueDatums.push(newDatum);
    });
  });
  addAnimation('drawing', function() {
    var n = 0;
    function moveDatam() {
        if(n == blueDatums.length)
          return;
        var d = blueDatums[n];
        n = n + 1;
        d.image.animate(100, '<>').move(d.image.x() - 158, d.image.y() + 166).after(moveDatam);
    }
    moveDatam();
  });
});

Animation.animate(function () {
  registerCanvas('vndes');
  var ring1 = registerSVG('vndes', '${image_path}/ring1.svg', 388, 388, 400, 70);
  var ring2 = registerSVG('vndes', '${image_path}/vnode-ring-1.svg', 388, 388, 400, 70);
  var ring3 = registerSVG('vndes', '${image_path}/vnode-ring-2.svg', 388, 388, 400, 70);
  var node1 = registerSVG('vndes', '${image_path}/node1.svg', 90, 90, 547, 60);
  var node2 = registerSVG('vndes', '${image_path}/node2.svg', 90, 90, 715, 225);
  var node3 = registerSVG('vndes', '${image_path}/node3.svg', 90, 90, 550, 385);
  var node4 = registerSVG('vndes', '${image_path}/node4.svg', 90, 90, -100, 225);

  var threeNodes = [node1, node2, node3];

  ring2.image.opacity(0);
  ring3.image.opacity(0);

  var datums = [[],[],[]];
  var blueDatums = [[], [], []];

  function fillNode(nodex, nodey, datumFileName, offset, datumsSlice)
  {
    var pi = 3.1415927;
    var twoPi = 2 * pi;
    for(numCircles = 1; numCircles <= 6; numCircles++)
    {
      var numDatumsThisCircle = numCircles * 4;
      var radius = numCircles * 6;
      for(i = 0; i < numDatumsThisCircle; i++)
      {
        var theta = (offset + i) * (twoPi / numDatumsThisCircle)
        var datum = registerSVG('vndes', datumFileName, 8, 8, nodex + 41 + radius * Math.cos(theta), nodey + 40 + radius * Math.sin(theta));
        datum.image.opacity(0);
        datumsSlice.push(datum);
      }
    }
  }
  fillNode(547, 0, '${image_path}/datum-yellow.svg', 0, datums[0]);
  fillNode(770, 225, '${image_path}/datum-blue.svg', 1, datums[1]); // node2
  fillNode(550, 438, '${image_path}/datum-bright-green.svg', 2, datums[2]); // node3

  addAnimation('vndes', function() {
    node1.image.animate().move(547, 0);
    node2.image.animate().move(770, 225);
    node3.image.animate().move(550, 438).after(function() {
      ring2.image.animate().opacity(1);
    });
  });
  addAnimation('vndes', function() {
    datums.forEach(function(d) {
      d.forEach(function(i) {
        i.image.animate().opacity(1);
      });
    });
  });
  addAnimation('vndes', function() {
    node4.image.animate().move(330, 225);
  });
  addAnimation('vndes', function() {
    ring3.image.animate().opacity(1);
  });
  addAnimation('vndes', function() {
    var j = 0;
    datums.forEach(function(datumArray) {
      var i = 0;
      newBlue = blueDatums[j++];
      datumArray.forEach(function(datum) {
        if(i++ % 4 != 0)
          return;
        var newDatum = registerSVG('vndes', '${image_path}/datum-green.svg', 8, 8, datum.image.x(), datum.image.y());
        datum.image.animate().opacity(0);
        newDatum.image.opacity(0);
        newDatum.image.animate().opacity(1);
        newBlue.push(newDatum);
      });
    });
  });
  addAnimation('vndes', function() {
    var j = 0;
    blueDatums.forEach(function(node) {
      var i = 0;
      var nodeGraphic = threeNodes[j++];
      var xDelta = node4.image.x() - nodeGraphic.image.x();
      var yDelta = node4.image.y() - nodeGraphic.image.y();
      function moveDatam() {
        if(node.length == 0)
          return;
        var d = node[i++];
        node.splice(0, 0);
        d.image.animate(200, '<>').move(d.image.x() + xDelta, d.image.y() + yDelta).after(moveDatam);
      };
      moveDatam();
    });
  });
});
