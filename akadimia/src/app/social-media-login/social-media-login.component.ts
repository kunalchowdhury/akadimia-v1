import {
  AfterViewInit,
  Component,
  ComponentFactoryResolver, ComponentRef,
  Input,
  NgZone,
  OnInit,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import {GoogleLoginProvider, FacebookLoginProvider, AuthService, LinkedinLoginProvider} from 'angular-6-social-login';
import { SocialLoginModule, AuthServiceConfig } from 'angular-6-social-login';
import { Socialusers } from '../Models/socialusers';
import { SocialloginService} from '../social-media-login.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import {delay} from "rxjs/operators";
import {async} from "rxjs-compat/scheduler/async";
import {SocialMediaLandingComponent} from "../social-media-landing/social-media-landing.component";
import {SimpleAuthServiceService} from "../simple-auth-service.service";

declare var FB: any;


@Component({
  selector: 'app-social-media-login',
  templateUrl: './social-media-login.component.html',
  styleUrls: ['./social-media-login.component.css']
})

export class SocialMediaLoginComponent implements OnInit {
  title = 'Akadimía Corporate';


  constructor(private cfr: ComponentFactoryResolver, private router: Router, private ngZone: NgZone, private socialAuthService: AuthService, private simpleAuthServiceService : SimpleAuthServiceService) {
  }

  ngOnInit() {

    (window as any).fbAsyncInit = function () {
      FB.init({
        appId: '225774565245007',
        cookie: true,
        xfbml: true,
        version: 'v3.1'
      });
      FB.AppEvents.logPageView();
    };

    (function (d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) {
        return;
      }
      js = d.createElement(s);
      js.id = id;
      js.src = "https://connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));

  }


  async geUrlName(url): Promise<string[]> {
    await delay(5000);
    return await FB.api(url, function (response) {
      //cur_name = response.name;
      //cur_email = response.email;
      alert(response.name);
      alert(response.email);
      return [response.name, response.email];
    });
  }

  public socialSignIn(socialPlatform: string) {
    let socialPlatformProvider;
    if (socialPlatform == "facebook") {
      socialPlatformProvider = FacebookLoginProvider.PROVIDER_ID;
    } else if (socialPlatform == "google") {
      socialPlatformProvider = GoogleLoginProvider.PROVIDER_ID;
    } else if (socialPlatform == "linkedin") {
      socialPlatformProvider = LinkedinLoginProvider.PROVIDER_ID;
    }

    /*this.socialAuthService.signIn(socialPlatformProvider).then(
      (userData) => {
        console.log(socialPlatform+" sign in data : " , userData);
        // Now sign-in with userData
        // ...

      }
    );*/

    console.log("submit login to facebook");
    // FB.login();
    FB.login((response) => {
      console.log('submitLogin', response);
      if (response.authResponse) {
        let url = '/me?fields=name,email';
        let cur_name , cur_email='unknown';
        let r = this.router;
        let aServ = this.simpleAuthServiceService;
        FB.api(url, function (response) {
          //cur_name = response.name;
          //cur_email = response.email;

          const accessToken = FB.getAuthResponse()['accessToken'];

          alert("Your logged in as => "+response.name +" with email id "+ response.email);
//          (<SocialMediaLandingComponent>componentRef).curUser = response.email;
          aServ.setAuthentication(true);
          localStorage.setItem("APP_TOKEN", accessToken);
          localStorage.setItem("LAST_LOGIN_TIME", String(new Date().getTime()));
          r.navigate(['social-media-landing', response.email], {replaceUrl: true});
          });

         /*FB.api(url, function (response) {
          //cur_name = response.name;
          //cur_email = response.email;
          alert(response.name);
          alert(response.email);
          //alert("Facebook login authenticated ");
          r.navigate(['profile-editor']);

        });*/
        //login success
        //login success code here
        //redirect to home page
        alert("Authenticating..Please wait..");


      } else {
        console.log('User login failed');
      }
    });
  }

}
