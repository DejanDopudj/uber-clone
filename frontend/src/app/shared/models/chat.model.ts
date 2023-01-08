import { Message } from './message.model';

export interface Chat {
  username: string;
  messages: Array<Message>;
  lastReadAdmin: Date;
  lastReadMember: Date;
}
