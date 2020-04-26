export class ErrorDialogData {

  constructor(title: string, error) {
    this.title = title;
    this.error = error;
  }

  title: string;
  error;
}
