export class ErrorDialogData {

  constructor(title: string, message: string | null, stacktrace?: string | null, path?: string) {
    this.title = title;
    this.message = message;
    this.stacktrace = stacktrace;
    this.path = path;
  }

  title: string;
  message: string | null;
  stacktrace: string | null | undefined;
  path: string | undefined;
}
