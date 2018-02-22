    var exec = require('cordova/exec');

    var SecurityProvider = {
        installIfNeededAsync: function(){
            var p = new Promise(function(resolve, reject) {
                        cb = function (result) {
                            if(result.providerInstalledUpdated === "not_available" || result.providerInstalledUpdated === "action_required"){
                                reject(result);
                            }
                            else if(result.providerInstalledUpdated === "installed"){
                                resolve(result);
                            }
                        }
                        exec(cb, cb, 'SecurityProvider', 'installIfNeededAsync', [""]);
                    }
                );

            return p;
        }

      };

    module.exports = SecurityProvider;