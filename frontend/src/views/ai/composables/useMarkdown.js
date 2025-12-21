import { marked } from 'marked';
import DOMPurify from 'dompurify';

export function useMarkdown() {
  const render = (text) => {
    const rawHtml = marked.parse(text ?? '', { breaks: true });
    return DOMPurify.sanitize(rawHtml);
  };

  return { render };
}
