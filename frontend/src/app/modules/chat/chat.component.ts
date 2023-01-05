import { Component, OnInit } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {
  indexOfSelectedChat: number = 0;
  displayChat = false;
  public newMessage = '';
  user = {
    username: 'Micko',
    role: 'ADMIN',
  };
  chats = [
    {
      username: 'Miladin Momcilovic Veliki',
      messages: [
        {
          sender: 'Micko',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
        {
          sender: 'Miladin',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
      ],
    },
    {
      username: 'Marko',
      messages: [
        {
          sender: 'Miladin',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
        {
          sender: 'Micko',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
      ],
    },
  ];
  constructor() {}

  addMessage(): void {
    if (this.newMessage.trim() === '') {
      return;
    }
    this.chats[this.indexOfSelectedChat].messages.push({
      sender: 'Micko',
      content: this.newMessage,
      sentDateTime: new Date(),
    });
    this.newMessage = '';
  }

  onValueChange(event: Event): void {
    const value = (event.target as any).value;
    if (value === '\n') {
      let objdiv = document.getElementById('chat-scroll');
      objdiv!.scrollTop = objdiv!.scrollHeight + 50;
      return;
    }
    this.newMessage = value;
  }

  changeMessages(username: String): void {
    for (let i = 0; i < this.chats.length; i++) {
      if (this.chats[i].username === username) {
        this.indexOfSelectedChat = i;
        break;
      }
    }
  }

  changeChatDisplay(): void {
    this.displayChat = !this.displayChat;
  }

  ngOnInit(): void {
    let conn = Stomp.over(new SockJS('http://localhost:8080/ws'));
    conn.connect({}, function () {
      conn.subscribe('/topic/private', function (message: any) {
        console.log(JSON.parse(message.body).connect);
      });
    });
  }
}
