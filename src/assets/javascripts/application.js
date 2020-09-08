//= require polyfill/Object.assign.js

//= require webjars.js
//= require portal.js

//= require inc/notification.js
//= require inc/menu.js
//= require inc/copy-to-clipboard.js
//= require inc/tabs.js
//= require inc/table-sort.js
//= require inc/accordion.js
//= require inc/message.js
//= require inc/file-input.js
//= require inc/char-count.js
//= require inc/api-reload.js

//= require vendor/isotope.min.js
//= require inc/tiles.js

//= require vendor/Sortable.min.js
//= require inc/table-drag.js

//= require vendor/itds.cookie-monster.js

//= require_self

var portal = new Portal();
itds.cookieMonster.init({
  defaultStyle: false,
});

itds.cookieMonster.setText({
  de: {
    message: 'Pfadibewegung Schweiz (PBS) verwendet Cookies und Analyse-Tools, um die Nutzerfreundlichkeit der Website zu verbessern. Durch die weitere Verwendung unserer Inhalte und Leistungen erklären Sie sich mit der Nutzung von Cookies einverstanden. Informationen zu Cookies finden Sie in unserer <a href="https://pfadi.swiss/de/f/impressum/" target="_blank" rel="noopener">Datenschutzerklärung</a>.',
    button: 'Akzeptieren'
  },
  fr: {
    message: 'Mouvement Scout de Suisse (MSdS) utilise des cookies et des outils d’analyse afin d’améliorer la convivialité du site Web. En poursuivant votre utilisation de nos contenus et prestations, vous déclarez accepter l’utilisation de ces cookies. Vous trouverez de plus amples informations concernant les cookies dans notre <a href="https://pfadi.swiss/fr/f/impressum/" target="_blank" rel="noopener">déclaration relative à la protection des données</a>.',
    button: 'Accepter'
  },
  it: {
    message: 'Movimento Scout Svizzero (MSS) utilizza cookie e tool di analisi per migliorare la facilità di uso del sito web. Continuando ad utilizzare i nostri contenuti e le nostre prestazioni vi dichiarate d’accordo con l’utilizzo dei cookie. Per ulteriori informazioni riguardo ai cookie e alla protezione dei dati nella nostra impresa cliccate su <a href="https://pfadi.swiss/it/f/colofone/" target="_blank" rel="noopener">protezione dei dati</a>.',
    button: 'Accettare'
  },
  en: {
    message: 'Mouvement Scout de Suisse (MSdS) uses cookies to optimise this website and make ongoing improvements. By using this website and its services and continuing to browse, you are agreeing to the use of cookies. You can change this in your browser settings. <a href="https://pfadi.swiss/fr/f/impressum/" target="_blank" rel="noopener">Privacy policy</a>.',
    button: 'Accept'
  }
});
