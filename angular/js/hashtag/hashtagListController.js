angular.module('tweetApp').controller('hashtagListController', ['hashtagService', function(hashtagService) {

    this.getAllTags = () => {
        hashtagService.getAllTags().then((done) =>{
            console.log(done.data)
            this.tags = done.data;
            return done.data;
        })
    }

}])