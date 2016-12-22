angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, spotcheckMismatchApi, mismatchSummaryApi) {

    $scope.datasource = 'OPENLEG';
    $scope.status = 'OPEN';

    $scope.mismatchSummary = {};

    $scope.billMismatches = {
        matches: [], // A master copy of all mismatches.
        filtered: [] // Mismatches which match the user specified filters.
    };

    $scope.onDatasourceChange = function () {
        // TODO: re query all content types?
        spotcheckMismatchApi.getBills($scope.datasource)
            .then(function (billMismatches) {
                $scope.billMismatches.matches = billMismatches;
                $scope.onStatusChange();
            });
    };

    ($scope.init = function () {
        console.log(referenceTypeMap);
        console.log(referenceTypeDisplayMap);
        console.log(referenceContentTypeMap);
        console.log(mismatchMap);

        // TODO: Date will prob be a url search param.
        $scope.date = moment().format('l');

        mismatchSummaryApi.get($scope.datasource)
            .then(function (mismatchSummary) {
                $scope.mismatchSummary = mismatchSummary;
            });

        $scope.onDatasourceChange();
    }).call();


    $scope.onStatusChange = function () {
        // TODO: Filter all mismatch content types?
        $scope.billMismatches.filtered = mismatchesWithStatus($scope.billMismatches.matches, $scope.status);
    };

    function mismatchesWithStatus(mismatches, status) {
        var filterByStatus = function (mismatch) {
            if (status === 'OPEN') {
                return mismatch.status === 'NEW' || mismatch.status === 'EXISTING';
            }
            return mismatch.status === status;
        };
        return mismatches.filter(filterByStatus)
    }
}
