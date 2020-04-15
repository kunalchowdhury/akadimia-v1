import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-corporate-login',
  templateUrl: './corporate-login.component.html',
  styleUrls: ['./corporate-login.component.css']
})
export class CorporateLoginComponent implements OnInit {
  title = 'Akadimía Corporate';
  isAuthenticated: boolean;

  constructor(private router: Router) {
    /*this.authService.isAuthenticated.subscribe(
      (isAuthenticated: boolean)  => this.isAuthenticated = isAuthenticated
    );*/
    this.isAuthenticated = false;
  }

  ngOnInit() {
    this.isAuthenticated = false ; // await this.authService.checkAuthenticated();
    // this.router.navigate(['']);
  }

  logout() {
    // this.authService.logout('/');
  }
}
