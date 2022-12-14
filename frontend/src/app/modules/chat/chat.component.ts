import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {
  indexOfSelectedChat: number = 0;
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

  ngOnInit(): void {}
}
