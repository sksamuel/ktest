(window.webpackJsonp=window.webpackJsonp||[]).push([[9],{159:function(e,t,n){"use strict";n.d(t,"a",(function(){return u})),n.d(t,"b",(function(){return m}));var r=n(0),o=n.n(r);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=o.a.createContext({}),p=function(e){var t=o.a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=p(e.components);return o.a.createElement(l.Provider,{value:t},e.children)},b={inlineCode:"code",wrapper:function(e){var t=e.children;return o.a.createElement(o.a.Fragment,{},t)}},d=o.a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),u=p(n),d=r,m=u["".concat(s,".").concat(d)]||u[d]||b[d]||i;return n?o.a.createElement(m,a(a({ref:t},l),{},{components:n})):o.a.createElement(m,a({ref:t},l))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,s=new Array(i);s[0]=d;var a={};for(var c in t)hasOwnProperty.call(t,c)&&(a[c]=t[c]);a.originalType=e,a.mdxType="string"==typeof e?e:r,s[1]=a;for(var l=2;l<i;l++)s[l]=n[l];return o.a.createElement.apply(null,s)}return o.a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},68:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return s})),n.d(t,"metadata",(function(){return a})),n.d(t,"rightToc",(function(){return c})),n.d(t,"default",(function(){return p}));var r=n(3),o=n(7),i=(n(0),n(159)),s={id:"koin",title:"Koin",sidebar_label:"Koin",slug:"koin.html"},a={unversionedId:"extensions/koin",id:"extensions/koin",isDocsHomePage:!1,title:"Koin",description:"Koin",source:"@site/docs/extensions/koin.md",slug:"/extensions/koin.html",permalink:"/docs/extensions/koin.html",editUrl:"https://github.com/kotest/kotest/blob/master/documentation/docs/extensions/koin.md",version:"current",sidebar_label:"Koin",sidebar:"extensions",previous:{title:"Current Instant Listeners",permalink:"/docs/extensions/instant.html"},next:{title:"WireMock",permalink:"/docs/extensions/wiremock.html"}},c=[{value:"Koin",id:"koin",children:[]}],l={rightToc:c};function p(e){var t=e.components,n=Object(o.a)(e,["components"]);return Object(i.b)("wrapper",Object(r.a)({},l,n,{components:t,mdxType:"MDXLayout"}),Object(i.b)("h2",{id:"koin"},"Koin"),Object(i.b)("p",null,"The ",Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://insert-koin.io/"}),"Koin DI Framework")," can be used with Kotest through the ",Object(i.b)("inlineCode",{parentName:"p"},"KoinListener")," test listener and its own interface ",Object(i.b)("inlineCode",{parentName:"p"},"KoinTest"),"."),Object(i.b)("p",null,"To add the listener to your project, add the dependency to your project:"),Object(i.b)("p",null,Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-koin"}),Object(i.b)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20release"})),"\n",Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-koin/"}),Object(i.b)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20snapshot"}))),Object(i.b)("pre",null,Object(i.b)("code",Object(r.a)({parentName:"pre"},{className:"language-kotlin"}),"io.kotest.extensions:kotest-extensions-koin:${version}\n")),Object(i.b)("p",null,"With the dependency added, we can use Koin in our tests!"),Object(i.b)("pre",null,Object(i.b)("code",Object(r.a)({parentName:"pre"},{className:"language-kotlin"}),'class KotestAndKoin : FunSpec(), KoinTest {\n\n    override fun listeners() = listOf(KoinListener(myKoinModule))\n\n    val userService by inject<UserService>()\n\n    init {\n      test("Use user service") {\n        userService.getUser().username shouldBe "LeoColman"\n      }\n    }\n\n}\n')))}p.isMDXComponent=!0}}]);