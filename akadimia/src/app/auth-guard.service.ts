import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from './auth.service';
import {SimpleAuthServiceService} from "./simple-auth-service.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(public authService: SimpleAuthServiceService, public router: Router) {}

  canActivate() {
    let diff = 60000;
    if(localStorage.APP_TOKEN && localStorage.LAST_LOGIN_TIME) {
      const token = localStorage.getItem("APP_TOKEN");
      const time = localStorage.getItem("LAST_LOGIN_TIME");
      diff = new Date().getTime() - Number(time);
    //  alert("diff - > "+diff);
    }

    if (!this.authService.isUserAuthenticated() && diff >= 60000) {
      alert("Invalid Login , Please login again");
      this.router.navigate(['social-media-login'], {replaceUrl: true});
      return false;
    }
    return true;
  }


  /*async canActivate() {
    if (!await this.authService.checkAuthenticated()) {
      await this.router.navigate(['login']);
      return false;
    }
    return true;
  }*/
}
