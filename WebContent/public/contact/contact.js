angular.module('contact', ['ngRoute'])
 
.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/contact', {
        templateUrl: 'public/contact/contact.html',
        controller: 'contact_Ctrl'
    });
}])
 
.controller('contact_Ctrl', ['$scope', function($scope) {
    console.log("contact controller"); 
}]);