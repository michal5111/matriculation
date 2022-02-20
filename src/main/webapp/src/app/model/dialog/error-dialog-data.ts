export class ErrorDialogData {

  constructor(title: string, message: string, stacktrace?: string, path?: string) {
    this.title = title;
    this.message = message;
    this.stacktrace = stacktrace;
    this.path = path;
  }

  title: string;
  message: string;
  stacktrace: string;
  path: string;
}
