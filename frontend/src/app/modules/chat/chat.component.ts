import { Component, OnInit } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';
import { ChatService } from 'src/app/core/http/user/chat.service';
import { Chat } from 'src/app/shared/models/chat.model';
import { User } from 'src/app/shared/models/user.model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {
  numOfNewMessages: number = 0;
  stompClient: any;
  indexOfSelectedChat: number = 0;
  displayChat: boolean = false;
  shouldScroll: boolean = false;
  newMessage: string = '';
  receiver: string = '';
  user: User = { username: '', role: '' };
  chats: Array<Chat> = [];
  constructor(
    private chatService: ChatService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this.initChats();
    if (this.chats.length === 0) return;
    this.countNumberOfUnreadMessages();
    this.initWS();
  }

  initChats(): void {
    const session = this.authenticationService.getSession();
    if (!session) return;
    if (session.accountType === 'ADMIN') {
      this.user = {
        username: 'admin',
        role: session.accountType,
      };
      this.chatService.getAllChats().then((response) => {
        this.chats = response.data;
      });
    } else {
      this.user = {
        username: session.username,
        role: session.accountType,
      };
      this.chatService.getUserChat(this.user.username).then((response) => {
        this.chats = response.data;
      });
    }
    this.receiver = this.chats[0].username;
  }

  initWS(): void {
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

  createNewMessage(event: Event): void {
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

  changeChat(username: String): void {
    for (let i = 0; i < this.chats.length; i++) {
      if (this.chats[i].username === username) {
        this.indexOfSelectedChat = i;
        this.receiver = this.chats[i].username;
        this.checkIfStillHasUnread();
        break;
      }
    }
  }

  changeChatDisplay(): void {
    this.displayChat = !this.displayChat;
    if (this.displayChat) {
      this.scroll();
      this.checkIfStillHasUnread();
    }
  }

  checkIfStillHasUnread(): void {
    let chat = this.chats[this.indexOfSelectedChat];
    this.numOfNewMessages -= this.getNUmberOfUnread(chat);
    this.updateLastRead();
  }

  updateLastRead(): void {
    if (this.user.role === 'ADMIN')
      this.chats[this.indexOfSelectedChat].lastReadAdmin = new Date();
    else this.chats[this.indexOfSelectedChat].lastReadMember = new Date();
  }

  getNUmberOfUnread(chat: any): number {
    let numOfUnread = 0;
    let lastRead =
      this.user.role === 'ADMIN' ? chat.lastReadAdmin : chat.lastReadMember;
    for (let i = chat.messages.length - 1; i > 0; i--) {
      if (chat.messages[i].sentDateTime > lastRead) numOfUnread += 1;
      else break;
    }
    return numOfUnread;
  }

  countNumberOfUnreadMessages(): void {
    for (let chat of this.chats) {
      this.numOfNewMessages += this.getNUmberOfUnread(chat);
    }
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
}
