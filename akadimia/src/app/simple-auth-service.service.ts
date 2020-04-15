import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SimpleAuthServiceService {

  public isAuthenticated = new BehaviorSubject<boolean>(false);

  setAuthentication(authenticated : boolean) {
    this.isAuthenticated.next(authenticated);
    return authenticated;
  }

  isUserAuthenticated() : boolean{
    return this.isAuthenticated.getValue();
  }

  constructor() { }
}
