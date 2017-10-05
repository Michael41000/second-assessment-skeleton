angular.module('tweetApp').controller('signInController', ['validateService', 'globalService', function (validateService, globalService) {
    this.globalService = globalService

    this.credentials = {}
    this.credentials.username
    this.credentials.password

    this.failedSignIn = false

    this.userLogin = () => {
        validateService.getCheckCredentials(this.credentials.username, this.credentials).then((done) => {
            if (done.data) { 
                this.globalService.primaryUser.credentials = this.credentials
                console.log(this.credentials)
                globalService.login(this.credentials.username)
                this.failedSignIn = false
                return done.data
            } else
                this.failedSignIn = true
        })
    }

}])