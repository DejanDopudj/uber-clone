import { Injectable } from '@angular/core';
import { Session } from 'src/app/shared/models/session.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor() { }

  getToken() {
    const token = localStorage.getItem('token');
    return token;
  }

  fetchSessionFromServer() {
    // getting whoami
    const session: Session | null = {
      username: 'driver1',
      name: 'Travis',
      surname: 'Bickle',
      profilePicture: null,
      accountType: 'driver',
      metadata: {
        active: false
      }
    }
    if (session) this.saveSession(session);
  }

  getSession() : Session | null {
    const sessionString: string | null = localStorage.getItem('session');
    if (sessionString)
      return JSON.parse(sessionString);
    return null;
  }

  getAccountType(): string {
    const session: Session | null = this.getSession();
    if (session){
      return session.accountType;
    }
    return "anonymous";
  }
  
  saveSession(session: Session) {
    localStorage.setItem('session', JSON.stringify(session));
  }

}
