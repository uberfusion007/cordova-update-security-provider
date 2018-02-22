
Android Security Provider Plugin for Apache Cordova
==================================

__Installation:__
`ionic cordova plugin add https://github.com/AbhishekJoshi/cordova-update-security-provider`


Read more about this topic: https://developer.android.com/training/articles/security-gms-provider.html 

__Usage:__

Should be used after `deviceready` event has fired, and before any senstive data has been attempted to be transmitted.

ES6+/Ionic:
```
      declare var SecurityProvider;

...
      SecurityProvider.installIfNeededAsync().then(data => {
        console.log('Everything is good: ' + data);
      }).catch(error => {
        console.log("Something is wrong with this device's Google Play Services");
        console.log('What is the error?: ' + error);
      });

```