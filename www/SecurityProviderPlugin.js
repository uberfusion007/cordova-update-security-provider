
    /*
     * Unless required by applicable law or agreed to in writing,
     * software distributed under the License is distributed on an
     * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     * KIND, either express or implied.  See the License for the
     * specific language governing permissions and limitations
     * under the License.
     *
     */

    var exec = require('cordova/exec');
    var Promise = require('promise');

    SecurityProvider.prototype.unregisterApplicationAsync = function ()
    {
        var deferral = new Promise.Deferral(),

            successCallback = function (result) {
                deferral.resolve(result);
            },

            errorCallback = function (err) {
                deferral.reject(err);
            };

        exec(successCallback, errorCallback, 'SecurityProvider', 'installIfNeededAsync', [this.notificationHubPath, this.connectionString]);

        return deferral.promise;
    }

    module.exports = SecurityProvider;