var exec = require('cordova/exec');

var Notes = {
  openNotesList: function (success, error) {
    exec(success, error, 'NotesPlugin', 'openNotesList', []);
  }
};

module.exports = Notes;
