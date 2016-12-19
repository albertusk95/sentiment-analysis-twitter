angular.module('whatisit', ['ngRoute'])
 
.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/whatisit', {
        templateUrl: 'public/whatisit/whatisit.html',
        controller: 'whatisit_Ctrl'
    });
}])
 
.controller('whatisit_Ctrl', ['$scope', function($scope) {
    console.log("whatisit controller"); 
}]);