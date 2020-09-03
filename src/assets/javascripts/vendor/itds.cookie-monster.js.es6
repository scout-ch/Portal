'use strict';

// Version: 1.0.6
// IMPORTANT: requires polyfill:
// polyfill/Object.assign.js

let itds = itds || {};

itds.cookieMonster = (function () {

  const htmlElem = document.getElementsByTagName('html')[0];

  let cookieElem;
  let cookieTextElem;
  let cookieBtnWrapperElem;
  let cookieBtnElem;
  let privacyText;
  let options;

  const defaultOptions = {
    appendTo: document.getElementsByTagName('body')[0],
    owner: htmlElem.getAttribute('data-wa-owner'),
    lang: getDocumentLang(),
    langFallback: 'de',
    privacyPageName: 'datenschutzerklaerung',
    cookieName: 'cookies-agreement',
    cookieIncludeSubdomain: false,
    popup: false,
    defaultStyle: true,
    style: {
      main: {},
      text: {},
      buttonWrapper: {},
      button: {},
      link: {},
    },
    htmlClass: {
      main: 'itds-privacy-notice',
      text: 'privacy-text',
      buttonWrapper: 'privacy-button',
      button: 'privacy-btn btn',
      link: null,
    },
  };

  function setDefaultStyleBasic() {
    const style = cookieElem.style;
    style.zIndex = Number.MAX_SAFE_INTEGER || 2147483647;   // 32-bit max integer
    style.position = 'fixed';
    style.color = '#000';
    style.background = '#FFF';
    style.lineHeight = '1.5';
  }

  function setDefaultStyleButton() {
    const style = cookieBtnElem.style;
    style.width = '200px';
    style.height = '35px';
    style.border = 'none';
    style.cursor = 'pointer';
    style.cssFloat = 'right';
    style.fontSize = '1em';
    style.color = '#000';
    style.background = '#cdcdcd';
  }

  function setDefaultStyleBanner() {
    const style = cookieElem.style;
    style.fontSize = '1em';
    style.bottom = '0';
    style.left = '0';
    style.right = '0';
    style.maxWidth = '100%';
    style.paddingTop = '1.5em';
    style.paddingRight = '3em';
    style.paddingBottom = '1.5em';
    style.paddingLeft = '3em';
    style.boxShadow = 'rgba(0, 0, 0, 0.1) 2px -4px 8px';
  }

  function setDefaultStylePopup() {
    const style = cookieElem.style;
    style.fontSize = '0.85rem';
    style.bottom = '0';
    style.right = '0';
    style.maxWidth = '400px';
    style.margin = '2em';
    style.padding = '2em';
    style.borderColor = '#CCC';
    style.border = '1px solid';
    style.boxShadow = 'rgba(0, 0, 0, 0.4) 6px 8px 8px'
  }

  function setDefaultStyleCookieBtnPopup() {
    const style = cookieBtnElem.style;
    style.marginTop = '1em';
  }

  function setDefaultStyleBtnWrapperBanner() {
    const style = cookieBtnWrapperElem.style;
    style.cssFloat = 'right';
    style.width = cookieBtnElem.style.width;
    style.padding = '1em';
  }

  function setDefaultStyleTextBanner() {
    const style = cookieTextElem.style;
    style.width = '80%';
    style.cssFloat = 'left';
    style.padding = '1em';
    style.minHeight = '35px';
  }

  function getDefaultText() {
    return {
      de: {
        message: `${options.owner} verwendet Cookies und Analyse-Tools, um die Nutzerfreundlichkeit der Website zu verbessern. Durch die weitere Verwendung unserer Inhalte und Leistungen erklären Sie sich mit der Nutzung von Cookies einverstanden. Informationen zu Cookies finden Sie in unserer <a href="/${options.lang}/${options.privacyPageName}">Datenschutzerklärung</a>.`,
        button: 'Akzeptieren'
      },
      fr: {
        message: `${options.owner} utilise des cookies et des outils d’analyse afin d’améliorer la convivialité du site Web. En poursuivant votre utilisation de nos contenus et prestations, vous déclarez accepter l’utilisation de ces cookies. Vous trouverez de plus amples informations concernant les cookies dans notre <a href="/${options.lang}/${options.privacyPageName}">déclaration relative à la protection des données</a>.`,
        button: 'Accepter'
      },
      it: {
        message: `${options.owner} utilizza cookie e tool di analisi per migliorare la facilità di uso del sito web. Continuando ad utilizzare i nostri contenuti e le nostre prestazioni vi dichiarate d’accordo con l’utilizzo dei cookie. Per ulteriori informazioni riguardo ai cookie e alla protezione dei dati nella nostra impresa cliccate su <a href="/${options.lang}/${options.privacyPageName}">protezione dei dati</a>.`,
        button: 'Accettare'
      },
      en: {
        message: `${options.owner} uses cookies and analytics tools to help improve your user experience. By continuing to use our website and services, you are agreeing to our use of cookies. More information about the use of cookies on our sites can be found in our <a href="/${options.lang}/${options.privacyPageName}">privacy policy</a>.`,
        button: 'Accept'
      }

    }
  }

  function getDocumentLang() {
    if (htmlElem.lang) {
      return htmlElem.lang
    }
    const matchPath = window.location.pathname.match(/(\/)(\w{2})(?=\/\w+)/);
    if (matchPath && matchPath.length === 3) {
      return matchPath[2]
    }
  }

  function getTextLang() {
    if (options.lang && options.lang in privacyText) {
      return options.lang;
    } else if (htmlElem.langFallback in privacyText) {
      return htmlElem.langFallback;
    } else if (options.langFallback in privacyText) {
      return options.langFallback;
    } else {
      return privacyText[Object.keys(privacyText)[0]];
    }
  }

  function createElement() {
    cookieElem = document.createElement('div');
    cookieTextElem = document.createElement('div');
    cookieBtnWrapperElem = document.createElement('div');
    cookieBtnElem = document.createElement('button');
    cookieBtnElem.onclick = function () {
      api.accept();
    };
    cookieBtnWrapperElem.appendChild(cookieBtnElem);
    cookieElem.appendChild(cookieTextElem);
    cookieElem.appendChild(cookieBtnWrapperElem);
    setElementText(getDefaultText());
  }

  function setDefaultStyles() {
    if (options.defaultStyle === true) {
      setDefaultStyleBasic();
      setDefaultStyleButton();
      if (options.popup === true) {
        setDefaultStylePopup();
        setDefaultStyleCookieBtnPopup()
      } else {
        setDefaultStyleBanner();
        setDefaultStyleBtnWrapperBanner();
        setDefaultStyleTextBanner();
      }
    }
  }

  function applyHtmlClasses() {
    addToClassList(
      cookieElem,
      options.htmlClass.main ? options.htmlClass.main : defaultOptions.htmlClass.main
    );

    addToClassList(
      cookieTextElem,
      options.htmlClass.text ? options.htmlClass.text : defaultOptions.htmlClass.text
    );

    addToClassList(
      cookieBtnWrapperElem,
      options.htmlClass.buttonWrapper ? options.htmlClass.buttonWrapper : defaultOptions.htmlClass.buttonWrapper
    );

    addToClassList(
      cookieBtnElem,
      options.htmlClass.button ? options.htmlClass.button : defaultOptions.htmlClass.button
    );

    let linkHtmlClass = options.htmlClass.link ? options.htmlClass.link : defaultOptions.htmlClass.link;
    [].slice.call(cookieTextElem.getElementsByTagName('a')).forEach(elem => {
      addToClassList(elem, linkHtmlClass);
    });
  }

  function applyOptionStyles() {
    if (isObjectNotEmpty(options.style.main)) {
      Object.assign(cookieElem.style, options.style.main);
    }

    if (isObjectNotEmpty(options.style.text)) {
      Object.assign(cookieTextElem.style, options.style.text);
    }

    if (isObjectNotEmpty(options.style.buttonWrapper)) {
      Object.assign(cookieBtnWrapperElem.style, options.style.buttonWrapper);
    }

    if (isObjectNotEmpty(options.style.button)) {
      Object.assign(cookieBtnElem.style, options.style.button);
    }

    if (isObjectNotEmpty(options.style.link)) {
      [].slice.call(cookieTextElem.getElementsByTagName('a')).forEach(elem => {
        Object.assign(elem.style, options.style.link);
      });
    }
  }

  function applyStylesAndHtmlClasses() {
    setDefaultStyles();
    applyOptionStyles();
    applyHtmlClasses();
  }

  function setElementText(text) {
    if (isObjectNotEmpty(text)) {
      privacyText = text;
      const lang = getTextLang();
      cookieTextElem.innerHTML = privacyText[lang].message;
      cookieBtnWrapperElem.getElementsByTagName('button')[0].innerText = privacyText[lang].button;
    }
  }

  function appendElementToBody() {
    document.body.appendChild(cookieElem);
  }

  function addToClassList(element, mainClass) {
    if (mainClass && mainClass.length > 0) {
      let classArray = mainClass.split(' ');
      if (!classArray) {
        return
      }
      const length = classArray.length;
      for (let i = 0; i < length; i++) {
        element.classList.add(classArray[i]);
      }
    }
  }

  function setConsent() {
    let domain = '';
    if (options.cookieIncludeSubdomain === true) {
      // try to match second and first level domain
      const hostnameArr = window.location.hostname.split('.');
      const hostnameArrLen = hostnameArr.length;

      if (hostnameArrLen > 1) {
        const sld = hostnameArr[hostnameArrLen - 2];
        const tld = hostnameArr[hostnameArrLen - 1];
        const numericalRegExp = new RegExp(/\d+/);

        if (!numericalRegExp.test(sld + tld)) {
          domain = ` domain=.${sld}.${tld}`;
        }
      }
    }
    document.cookie = `${options.cookieName}=1; expires=Sun, 10 Jan 2038 07:59:59 UTC; path=/;${domain}`;
  }

  function hasConsent() {
    const getValuePattern = new RegExp('(?:(?:^|.*;\\s*)' + options.cookieName + '\\s*\\=\\s*([^;]*).*$)|^.*$');
    return document.cookie != null &&
      document.cookie.length > 0 &&
      document.cookie.indexOf(options.cookieName) !== -1 &&
      document.cookie.replace(getValuePattern, "$1") === '1';
  }

  function isObjectNotEmpty(obj) {
    return obj && obj.constructor === Object && Object.keys(obj).length > 0
  }

  const api = {
    init(opts) {
      options = Object.assign({}, defaultOptions, opts);
      if (hasConsent()) {
        return;
      }
      createElement();
      applyStylesAndHtmlClasses();
      appendElementToBody();
    },
    getOptions() {
      return options;
    },
    setText(text) {
      if (hasConsent()) {
        return;
      }
      setElementText(text);
      applyStylesAndHtmlClasses();
    },
    show() {
      cookieElem.style.display = 'initial';
    },
    hide() {
      cookieElem.style.display = 'none';
    },
    accept() {
      setConsent();
      this.hide();
    }
  };

  return api;

})();
