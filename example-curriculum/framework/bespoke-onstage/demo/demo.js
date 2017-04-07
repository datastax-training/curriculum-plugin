var isWebKit = 'webkitAppearance' in document.documentElement.style;
bespoke.from('.deck', [
  bespoke.plugins.classes(),
  bespoke.plugins.keys(),
  bespoke.plugins.scale(isWebKit ? 'zoom' : 'transform'),
  bespoke.plugins.bullets('.bullet'),
  bespoke.plugins.onstage()
]);
