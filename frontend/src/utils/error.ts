export function toErrorMessage(exception: unknown): string {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}
