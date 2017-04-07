'use strict';

var browserify = require('browserify'),
  buffer = require('vinyl-buffer'),
  connect = require('gulp-connect'),
  del = require('del'),
  gulp = require('gulp'),
  gutil = require('gulp-util'),
  plumber = require('gulp-plumber'),
  rename = require('gulp-rename'),
  source = require('vinyl-source-stream'),
  through = require('through'),
  uglify = require('gulp-uglify'),
  isDist = process.argv.indexOf('watch') === -1,
  // browserifyPlumber fills the role of plumber() when working with browserify
  browserifyPlumber = function(e) {
    if (isDist) throw e;
    gutil.log(e.stack);
    this.emit('end');
  };

gulp.task('js', ['clean:js'], function() {
  // see https://wehavefaces.net/gulp-browserify-the-gulp-y-way-bb359b3f9623
  return browserify('scripts/src/main.js').bundle()
    .on('error', browserifyPlumber)
    .pipe(source('src/scripts/main.js'))
    .pipe(buffer())
/*    .pipe(isDist ? uglify() : through())*/
    .pipe(rename('curriculum.js'))
    .pipe(gulp.dest('scripts/dist'))
    .pipe(connect.reload());
});

gulp.task('clean:js', function() {
  return del('scripts/dist/curriculum.js');
});

gulp.task('watch', function() {
  gulp.watch('scripts/src/*.js', ['js']);
  gulp.watch('bespoke-animation/src/*.js', ['js']);
});

gulp.task('build', ['js']);

gulp.task('default', ['build']);
