// Include gulp
var gulp = require('gulp');

// Include Our Plugins
var jshint = require('gulp-jshint');
var sass = require('gulp-sass');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var gutil = require('gulp-util');
var buffer = require('vinyl-buffer');
var connect = require('gulp-connect');
var del = require('del');

var isDist = process.argv.indexOf('deploy') >= 0;

gulp.task('lint', function() {
    return gulp.src('src/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

gulp.task('sass', function() {
    return gulp.src('scss/*.scss')
        .pipe(sass())
        .pipe(gulp.dest('dist/css'));
});

gulp.task('compile', function() {
  return browserify('src/index.js').bundle()
    .on('error', function(e) { if (isDist) { throw e; } else { gutil.log(e.stack); this.emit('end'); } })
    .pipe(source('src/index.js'))
    .pipe(buffer())
    // .pipe(isDist ? closureCompiler(closureCompilerOpts) : uglify())
    .pipe(rename('bespoke-animation.js'))
    .pipe(gulp.dest('dist'))
    .pipe(connect.reload());
});

gulp.task('watch', function() {
    gulp.watch('src/*.js', ['lint', 'compile']);
    gulp.watch('scss/*.scss', ['sass']);
});

gulp.task('clean', function() {
  return del('dist');
});

gulp.task('default', ['lint', 'sass', 'compile']);
