Function.prototype.bind = Function.prototype.bind || require('function-bind');

var bespoke = require('bespoke'),
  bullets = require('bespoke-bullets'),
  onstage = require('../../lib/bespoke-onstage.js');

describe('bespoke-onstage', function() {

  var deck,
    client,
    setup = function() {
      var iframe = document.createElement('iframe');
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
      client = iframe.contentWindow;
    },
    createDeck = function() {
      document.title = 'bespoke-onstage tests';
      var parent = document.createElement('article');
      for (var i = 1; i <= 5; i++) {
        var section = document.createElement('section');
        var heading = document.createElement('h1');
        heading.appendChild(document.createTextNode('Slide ' + i));
        section.appendChild(heading);
        if (i === 1) {
          var notes = document.createElement('aside');
          notes.setAttribute('role', 'note');
          notes.appendChild(document.createTextNode('Note for slide ' + i + '.'));
          section.appendChild(notes);
        }
        else if (i === 3) {
          var ul = document.createElement('ul');
          for (var j = 1; j <= 3; j++) {
            var li = document.createElement('li');
            li.appendChild(document.createTextNode('Item ' + j));
            ul.appendChild(li);
          }
          section.appendChild(ul);
        }
        parent.appendChild(section);
      }
      document.body.appendChild(parent);
      deck = bespoke.from(parent, [
        bullets('li'),
        onstage()
      ]);
    },
    destroyDeck = function() {
      deck.fire('destroy');
      deck.parent.parentNode.removeChild(deck.parent);
      deck = null;
    },
    captureMessages = function(e) {
      this.messages.push(e.data);
    };

  beforeAll(setup);

  describe('message', function() {
    beforeEach(function() {
      createDeck();
      client.messages = [];
      client.addEventListener('message', captureMessages, false);
    });

    afterEach(function() {
      client.removeEventListener('message', captureMessages, false);
      destroyDeck();
    });

    it('should send back ack when REGISTER command is received', function(done) {
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        expect(client.messages.length).toBe(2);
        expect(client.messages[0]).toBe('REGISTERED bespoke-onstage%20tests 5');
        expect(client.messages[1]).toBe('CURSOR 1.0');
        done();
      }, 100);
    });

    it('should send back default title when REGISTER command is received and document has no title', function(done) {
      document.title = '';
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        expect(client.messages.length).toBe(2);
        expect(client.messages[0]).toBe('REGISTERED Untitled 5');
        expect(client.messages[1]).toBe('CURSOR 1.0');
        done();
      }, 100);
    });

    it('should advance to next slide when FORWARD command is received', function(done) {
      expect(deck.slide()).toBe(0);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('FORWARD', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(1);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 2.0');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to previous slide when BACK command is received', function(done) {
      deck.slide(4);
      expect(deck.slide()).toBe(4);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('BACK', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(3);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 4.0');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to next bullet when FORWARD command is received on slide with bullets', function(done) {
      deck.slide(2);
      expect(deck.slide()).toBe(2);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('FORWARD', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(2);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 3.1');
          expect(deck.slides[2].querySelector('li:nth-of-type(2)').classList).toContain('bespoke-bullet-current');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to first slide when START command is received', function(done) {
      deck.slide(4);
      expect(deck.slide()).toBe(4);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('START', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(0);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 1.0');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to last slide when END command is received', function(done) {
      expect(deck.slide()).toBe(0);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('END', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(4);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 5.0');
          done();
        }, 100);
      }, 100);
    });

    it('should retrieve encoded notes for slide when GET_NOTES command is received', function(done) {
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('GET_NOTES', '*');");
        setTimeout(function() {
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('NOTES Note%20for%20slide%201.');
          done();
        }, 100);
      }, 100);
    });

    it('should retrieve empty notes for slide without notes when GET_NOTES command is received', function(done) {
      deck.slide(1);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('GET_NOTES', '*');");
        setTimeout(function() {
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('NOTES ');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to specified slide when SET_CURSOR command is received', function(done) {
      expect(deck.slide()).toBe(0);
      window.postMessage('SET_CURSOR 3.0', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(2);
        done();
      }, 100);
    });

    it('should advance to next slide when SET_CURSOR command is received with extra step', function(done) {
      expect(deck.slide()).toBe(0);
      window.postMessage('SET_CURSOR 4.1', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(4);
        done();
      }, 100);
    });

    it('should advance to next bullet on slide when SET_CURSOR command is received', function(done) {
      expect(deck.slide()).toBe(0);
      window.postMessage('SET_CURSOR 3.1', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(2);
        expect(deck.slides[2].querySelector('li:nth-of-type(2)').classList).toContain('bespoke-bullet-current');
        done();
      }, 100);
    });

    it('should advance to next slide when SET_CURSOR command is received with step that exceeds bullets', function(done) {
      expect(deck.slide()).toBe(0);
      client.eval("parent.postMessage('REGISTER', '*');");
      setTimeout(function() {
        client.messages = [];
        client.eval("parent.postMessage('SET_CURSOR 3.3', '*');");
        setTimeout(function() {
          expect(deck.slide()).toBe(3);
          expect(client.messages.length).toBe(1);
          expect(client.messages[0]).toBe('CURSOR 4.0');
          done();
        }, 100);
      }, 100);
    });

    it('should advance to specified slide when SET_CURSOR command is received with integer value', function(done) {
      expect(deck.slide()).toBe(0);
      window.postMessage('SET_CURSOR 3', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(2);
        done();
      }, 100);
    });

    it('should advance to first slide when SET_CURSOR command is received with zero value', function(done) {
      deck.slide(2);
      expect(deck.slide()).toBe(2);
      window.postMessage('SET_CURSOR 0.0', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(0);
        done();
      }, 100);
    });

    it('should advance to first slide when SET_CURSOR command is received with no value', function(done) {
      deck.slide(2);
      expect(deck.slide()).toBe(2);
      window.postMessage('SET_CURSOR', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(0);
        done();
      }, 100);
    });

    it('should advance to last slide when SET_CURSOR command is received and value exceeds number of slides', function(done) {
      expect(deck.slide()).toBe(0);
      window.postMessage('SET_CURSOR 100.0', '*');
      setTimeout(function() {
        expect(deck.slide()).toBe(4);
        done();
      }, 100);
    });
  });
});
