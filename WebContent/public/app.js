/** Application modules
*	ngRoute - routes the request to the appropriate page
*	queryContainer - serves the main page for getting the user's query
*	whatisit - serves the page for explanation about the usage of the application
*	contact - serves the page for explanation about the developer
*	primary - serves the page for displaying the result of analysis
*/
var app = angular.module('saitweet', [
    'ngRoute',
    'queryContainer',
	'whatisit',
	'contact',
	'primary'
])

// default router (used for the first load)
.config(['$routeProvider', function($routeProvider) {
    $routeProvider.otherwise({
        redirectTo: '/queryContainer'
    });
}]);

