import { Component, OnInit } from '@angular/core';
import { SocialLoginModule, AuthServiceConfig, AuthService } from 'angular-6-social-login';
import { Socialusers } from '../Models/socialusers'
import {SocialloginService} from '../social-media-login.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-social-media-dashboard',
  templateUrl: './social-media-dashboard.component.html',
  styleUrls: ['./social-media-dashboard.component.css']
})
export class SocialMediaDashboardComponent implements OnInit {
  socialusers = new Socialusers();
  constructor(public OAuth: AuthService,    private router: Router) { }
  ngOnInit() {
    this.socialusers = JSON.parse(localStorage.getItem('socialusers'));
    console.log(this.socialusers.image);
  }
  logout() {
    this.OAuth.signOut().then(data => {
      this.router.navigate(['/Login']);
    });
  }
}
