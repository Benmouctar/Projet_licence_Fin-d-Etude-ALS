import React, { useMemo } from 'react';
import { SafeHtmlRenderer } from './SafeHtmlRenderer';

/**
 * Reusable React presentation component that renders the Markdown Renderer user interface element.
 */
interface Props {
  content: string;
  className?: string;
  allowTags?: string[];
}





export const MarkdownRenderer: React.FC<Props> = ({ content, className, allowTags }) => {
  const html = useMemo(() => {
    let text = content;

    
    text = text.replace(/```[\w]*\n?([\s\S]*?)```/g, '<pre><code>$1</code></pre>');

    
    text = text.replace(/`([^`]+)`/g, '<code>$1</code>');

    
    text = text.replace(/^### (.+)$/gm, '<h3>$1</h3>');
    text = text.replace(/^## (.+)$/gm, '<h2>$1</h2>');

    
    text = text.replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>');
    text = text.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
    text = text.replace(/\*(.+?)\*/g, '<em>$1</em>');

    
    text = text.replace(/^> (.+)$/gm, '<blockquote>$1</blockquote>');

    
    text = text.replace(/^[-*] (.+)$/gm, '<li>$1</li>');
    text = text.replace(/(<li>.*<\/li>(\n|$))+/g, '<ul>$&</ul>');

    
    text = text.replace(/^\d+\. (.+)$/gm, '<li>$1</li>');

    
    text = text
      .split('\n')
      .map(line => {
        const trimmed = line.trim();
        if (!trimmed) return '';
        if (/^<(h2|h3|ul|ol|li|pre|blockquote|p)/.test(trimmed)) return trimmed;
        return `<p>${trimmed}</p>`;
      })
      .join('');

    return text;
  }, [content]);

  return (
    <SafeHtmlRenderer 
      html={html} 
      className={className} 
      allowTags={allowTags}
    />
  );
};
