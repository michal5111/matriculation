export class ErrorDialogData {

  constructor(title: string, error, stacktrace?: string) {
    this.title = title;
    this.error = error;
    this.stacktrace = stacktrace;
  }

  title: string;
  error;
  stacktrace: string;
}
