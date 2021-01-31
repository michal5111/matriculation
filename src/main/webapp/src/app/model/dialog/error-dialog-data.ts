export class ErrorDialogData {

  constructor(title: string, message: string, stacktrace?: string) {
    this.title = title;
    this.message = message;
    this.stacktrace = stacktrace;
  }

  title: string;
  message: string;
  stacktrace: string;
}
