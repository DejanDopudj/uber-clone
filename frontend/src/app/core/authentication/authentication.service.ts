import { Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor() { }

  getToken() {
    const token = localStorage.getItem('token');
    return token;
  }

}
