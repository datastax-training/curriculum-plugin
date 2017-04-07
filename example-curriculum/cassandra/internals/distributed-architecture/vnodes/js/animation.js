'use strict';

(function () {

  var speed = 250;

  function fillNodeWithHiddenDatums(insertImage, node, datumFileName, numCircles, step) {

    var datumSize = 8;
    var datums = [];

    for (var layer = 1; layer <= 6; layer += 1) {

      var howManyDatums = layer * numCircles;
      var radius = layer * 6;

      for (var i = 0; i < howManyDatums; i += step) {

        var theta = i * (2 * Math.PI / howManyDatums);
        var x = node.cx() - (datumSize / 2) + radius * Math.cos(theta);
        var y = node.cy() - (datumSize / 2) + radius * Math.sin(theta);

        var datum = insertImage(datumFileName, datumSize, datumSize, x, y, 0);
        datums.push(datum);
      }
    }

    return datums;
  }

  Animation.registerAnimatedCanvas('drawing', function (insertImage, group) {

    var ring1 = insertImage('${image_path}/ring1.svg', 388, 388, 400, 70);
    var ring2 = insertImage('${image_path}/ring2.svg', 388, 388, 400, 70, 0);

    var node1 = insertImage('${image_path}/node1.svg', 90, 90, 547, 60);
    var node2 = insertImage('${image_path}/node2.svg', 90, 90, 715, 225);
    var node3 = insertImage('${image_path}/node3.svg', 90, 90, 550, 385);

    var node1Datums = fillNodeWithHiddenDatums(insertImage, node1, '${image_path}/datum-yellow.svg', 6, 1);
    var node2Datums = fillNodeWithHiddenDatums(insertImage, node2, '${image_path}/datum-blue.svg', 6, 2);
    var node3Datums = fillNodeWithHiddenDatums(insertImage, node3, '${image_path}/datum-bright-green.svg', 6, 2);

    var node4Y = 225;
    var node4X = 390;
    var node4 = insertImage('${image_path}/node4.svg', 90, 90, -100, node4Y);

    var datumsToHide = node1Datums.filter(function (datum, i) {
      return i % 2 !== 0;
    });
    var node1GreenDatums = datumsToHide.map(function (datum) {
      return insertImage('${image_path}/datum-green.svg', 8, 8, datum.x(), datum.y())
        .opacity(0);
    });

    return [
      function () {
        return group(node1Datums, node2Datums, node3Datums).animate(speed).opacity(1);
      },
      function () {
        return node4.animate(speed).x(300);
      },
      function () {
        return node4.animate(speed).x(node4X);
      },
      function () {
        return ring2.animate(speed).opacity(1);
      },
      function () {
        return [
          group(datumsToHide).animate(speed).opacity(0),
          group(node1GreenDatums).animate(speed).opacity(1),
        ];
      },
      function () {
        return node1GreenDatums.map(function (datum, i) {
          var xShift = node4X - node1.x();
          var yShift = node4Y - node1.y();
          return datum.animate(50, '<>', 50 * i).dmove(xShift, yShift);
        });
      },
    ];
  });

  Animation.registerAnimatedCanvas('vndes', function (insertImage, group) {

    var ring1 = insertImage('${image_path}/ring1.svg', 388, 388, 400, 70);
    var ring2 = insertImage('${image_path}/vnode-ring-1.svg', 388, 388, 400, 70, 0);
    var ring3 = insertImage('${image_path}/vnode-ring-2.svg', 388, 388, 400, 70, 0);

    var nodes = [
      insertImage('${image_path}/node1.svg', 90, 90, 547, 60),
      insertImage('${image_path}/node2.svg', 90, 90, 715, 225),
      insertImage('${image_path}/node3.svg', 90, 90, 550, 385),
    ];
    var node4 = insertImage('${image_path}/node4.svg', 90, 90, -100, 225);

    var nodeDatums = [
      fillNodeWithHiddenDatums(insertImage, nodes[0], '${image_path}/datum-yellow.svg', 4, 1),
      fillNodeWithHiddenDatums(insertImage, nodes[1], '${image_path}/datum-blue.svg', 4, 1),
      fillNodeWithHiddenDatums(insertImage, nodes[2], '${image_path}/datum-bright-green.svg', 4, 1),
    ];

    var datumsToHide = nodeDatums.map(function (datums, nodeIdx) {
      return datums.filter(function (d, i) {
        return (i + nodeIdx) % 4 === 0;
      });
    });

    var greenDatums = datumsToHide.map(function (datums, nodeIdx) {
      return datums.map(function (d) {
        return insertImage('${image_path}/datum-green.svg', 8, 8, d.x(), d.y(), 0);
      });
    });

    return [
      function () {
        return [
          group(nodes[0], nodeDatums[0], greenDatums[0]).animate(speed).dy(-60),
          group(nodes[1], nodeDatums[1], greenDatums[1]).animate(speed).dx(54),
          group(nodes[2], nodeDatums[2], greenDatums[2]).animate(speed).dy(53),
          ring2.animate(speed, '<>', speed).opacity(1),
        ];
      },
      function () {
        return group(nodeDatums[0], nodeDatums[1], nodeDatums[2]).animate(speed).opacity(1);
      },
      function () {
        return node4.animate(speed).x(330);
      },
      function () {
        return [
          ring1.opacity(0),
          ring2.animate(speed).opacity(0),
          ring3.animate(speed, '<>', speed).opacity(1),
        ];
      },
      function () {
        return [
          group(greenDatums[0], greenDatums[1], greenDatums[2]).animate(speed).opacity(1),
          group(datumsToHide[0], datumsToHide[1], datumsToHide[2]).animate(speed).opacity(0),
        ];
      },
      function () {

        var anims = greenDatums.map(function (datums, nodeIdx) {
          var node = nodes[nodeIdx];
          var xDelta = node4.x() - node.x();
          var yDelta = node4.y() - node.y();
          return datums.map(function (d, i) {
            return d.animate(100, '<>', i * 100 + nodeIdx * 33).dmove(xDelta, yDelta);
          });
        });

        return [].concat(anims[0], anims[1], anims[2]);
      },
    ];
  });
})();
