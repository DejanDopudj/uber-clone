import { Component, OnInit } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {
  numOfNewMessages = 0;
  stompClient: any;
  indexOfSelectedChat: number = 1;
  displayChat = false;
  shouldScroll = false;
  public newMessage = '';
  receiver = 'Miladin';
  user = {
    username: 'admin',
    role: 'ADMIN',
  };
  chats = [
    {
      username: 'Miladin',
      messages: [
        {
          sender: 'admin',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
        {
          sender: 'Miladin',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
      ],
      lastReadAdmin: new Date(),
      lastReadMember: new Date(),
    },
    {
      username: 'Marko',
      messages: [
        {
          sender: 'Marko',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
        {
          sender: 'Micko',
          content: 'Ovo je poruka',
          sentDateTime: new Date(),
        },
      ],
      lastReadAdmin: new Date(),
      lastReadMember: new Date(),
    },
  ];
  constructor() {}

  addMessage(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.newMessage.trim() === '') {
      return;
    }
    let currentDate = new Date();
    let tempDate = new Date();
    tempDate.setHours(tempDate.getHours() + 1);
    this.stompClient.send(
      '/app/privateMessage',
      {},
      JSON.stringify({
        type: 'CHAT',
        sender: this.user.username,
        receiver: this.receiver,
        content: this.newMessage.trim(),
        sentDateTime: tempDate,
      })
    );
    this.chats[this.indexOfSelectedChat].messages.push({
      sender: this.user.username,
      content: this.newMessage,
      sentDateTime: currentDate,
    });
    this.newMessage = '';
    this.scroll();
    this.updateLastRead();
  }

  onValueChange(event: Event): void {
    const value = (event.target as any).value;
    this.newMessage = value;
  }

  changeMessages(username: String): void {
    for (let i = 0; i < this.chats.length; i++) {
      if (this.chats[i].username === username) {
        this.indexOfSelectedChat = i;
        this.receiver = this.chats[i].username;
        this.checkIfStillUnreed();
        break;
      }
    }
  }

  changeChatDisplay(): void {
    this.displayChat = !this.displayChat;
    if (this.displayChat) {
      this.scroll();
      this.checkIfStillUnreed();
    }
  }

  checkIfStillUnreed(): void {
    let chat = this.chats[this.indexOfSelectedChat];
    this.numOfNewMessages -= this.numberOfUndreadMess(chat);
    this.updateLastRead();
  }

  updateLastRead(): void {
    if (this.user.role === 'ADMIN')
      this.chats[this.indexOfSelectedChat].lastReadAdmin = new Date();
    else this.chats[this.indexOfSelectedChat].lastReadMember = new Date();
  }

  numberOfUndreadMess(chat: any): number {
    let numOfUnread = 0;
    let lastRead =
      this.user.role === 'ADMIN' ? chat.lastReadAdmin : chat.lastReadMember;
    for (let i = chat.messages.length - 1; i > 0; i--) {
      if (chat.messages[i].sentDateTime > lastRead) numOfUnread += 1;
      else break;
    }
    return numOfUnread;
  }

  scroll(): void {
    setTimeout(() => {
      let objdiv = document.getElementById('chat-scroll');
      if (objdiv) objdiv!.scrollTo(0, objdiv!.scrollHeight);
    }, 25);
  }

  checkMessages(sender: string): void {
    if (!this.displayChat) {
      this.numOfNewMessages += 1;
    } else if (this.chats[this.indexOfSelectedChat].username !== sender) {
      this.numOfNewMessages += 1;
    } else {
      this.numOfNewMessages = 0;
    }
  }

  ngOnInit(): void {
    let addr = '';
    if (this.user.role === 'ADMIN') {
      addr = '/user/admin/private';
    } else {
      addr = '/user/' + this.user.username + '/private';
    }
    this.stompClient = Stomp.over(new SockJS('http://localhost:8080/ws'));
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe(addr, (message: any) => {
        let messageData = JSON.parse(message.body);
        let mess = {
          sender: messageData.sender,
          content: messageData.content,
          sentDateTime: new Date(messageData.sentDateTime),
        };
        if (messageData.type === 'CHAT') {
          for (let chat of this.chats) {
            if (chat.username === messageData.sender) {
              chat.messages.push(mess);
            }
          }
          this.checkMessages(mess.sender);
          this.scroll();
          this.updateLastRead();
        }
      });
    });
  }
}
