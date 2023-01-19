import { Injectable } from '@angular/core';
import { CompatClient, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Session } from 'src/app/shared/models/session.model';
import { AuthenticationService } from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class SocketService {
  stompClient!: CompatClient;

  constructor(private authenticationService: AuthenticationService) {}

  initWS(): Promise<void> {
    const session: Session | null = this.authenticationService.getSession();
    if (!session) return new Promise((resolve) => { resolve() });
    return new Promise((resolve) => {
      this.stompClient = Stomp.over(new SockJS('http://localhost:8080/ws'));
      this.stompClient.connect({}, () => { resolve() });
    });
  }


}
