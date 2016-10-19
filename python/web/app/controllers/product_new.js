app.controller('ProductNewCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.product = {};

    $scope.new = function() {
        service.add($scope.product, $scope.clear);
    };

    $scope.clear = function() {
        $scope.product = {};

        jQuery('.form-product .product-image .add-image').show();
        jQuery('.form-product .product-image .new-image').hide();
        jQuery('.form-product .product-image .new-image img').attr('src', '#');
    };

    $scope.addImage = function() {
        jQuery('.form-product .product-image input[type=file]').click();
    };

    jQuery('.form-product .product-image input[type=file]').change(function() {
        jQuery('.form-product .product-image .add-image').hide();
        jQuery('.form-product .product-image .new-image').show();

        input = this;
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                jQuery('.form-product .product-image .new-image img').attr('src', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
        }
    });
}]);