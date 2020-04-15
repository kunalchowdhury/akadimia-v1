import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {speedDialFabAnimations} from './speed-dial-fab.animations';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: speedDialFabAnimations

})

export class AppComponent implements OnInit, OnDestroy {

  fabButtons = [
    {
      icon: 'apartment'
    },
    {
      icon: 'person'

    }
  ];
  buttons = [];
  fabTogglerState = 'inactive';

  constructor(private router: Router, private ngZone: NgZone) {

  }

  showItems() {
    this.fabTogglerState = 'active';
    this.buttons = this.fabButtons;
  }

  ngOnInit(): void {

  }

  ngOnDestroy(): void {
    this.fabTogglerState = 'inactive';
  }

  hideItems() {
    this.fabTogglerState = 'inactive';
    this.buttons = [];
  }

  onToggleFab() {
    this.buttons.length ? this.hideItems() : this.showItems();
  }

  route(icon: string) {
    this.hideItems();
    // tslint:disable-next-line:triple-equals
    if (icon == 'apartment') {
      this.router.navigate(['corporate-login'], {replaceUrl: true});
    }
    // tslint:disable-next-line:triple-equals
    else if (icon == 'person') {
      this.router.navigate(['social-media-login'], {replaceUrl: true});
    }
  }
}
