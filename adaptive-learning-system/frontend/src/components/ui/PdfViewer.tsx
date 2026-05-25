import React, { useState } from 'react';
import { FileText, Download, Loader2 } from 'lucide-react';
import api from '../../services/api';

/**
 * Reusable React presentation component that renders the Pdf Viewer user interface element.
 */
interface Props {
  
  url: string;
  
  title?: string;
}










export const PdfViewer: React.FC<Props> = ({ url, title = 'PDF Document' }) => {
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  
  const filename = (() => {
    try {
      const segments = url.split('/');
      const last = segments[segments.length - 1];
      return last || title;
    } catch {
      return title;
    }
  })();

  const handleDownload = async () => {
    setDownloading(true);
    setError(null);
    try {
      const res = await api.get(url, {
        responseType: 'blob',
        baseURL: '',
      });

      const blob = new Blob([res.data], { type: 'application/pdf' });
      const objectUrl = URL.createObjectURL(blob);

      
      const anchor = document.createElement('a');
      anchor.href = objectUrl;
      anchor.download = filename;
      document.body.appendChild(anchor);
      anchor.click();
      document.body.removeChild(anchor);

      
      setTimeout(() => URL.revokeObjectURL(objectUrl), 3000);
    } catch (err) {
      console.error('[PdfViewer] Download failed:', err);
      setError('Failed to download the PDF. Please try again.');
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="flex flex-col items-center gap-4 py-10 px-6">
      {}
      <div className="flex flex-col items-center gap-3 p-8 rounded-2xl border-2 border-dashed border-slate-200 dark:border-white/10 bg-slate-50 dark:bg-white/5 w-full max-w-sm">
        <div className="w-14 h-14 rounded-2xl bg-red-50 dark:bg-red-500/10 flex items-center justify-center">
          <FileText className="w-7 h-7 text-red-500" />
        </div>
        <div className="text-center">
          <p className="text-sm font-bold text-slate-800 dark:text-white truncate max-w-[220px]">
            {filename}
          </p>
          <p className="text-xs text-slate-400 dark:text-slate-500 mt-0.5">PDF Document</p>
        </div>
      </div>

      {}
      <button
        id="pdf-download-btn"
        onClick={handleDownload}
        disabled={downloading}
        className="inline-flex items-center gap-2 px-6 py-3 rounded-xl font-bold text-sm
          bg-emerald-500 hover:bg-emerald-600 disabled:opacity-60 disabled:cursor-not-allowed
          text-white transition-all shadow-md hover:shadow-emerald-500/30 hover:shadow-lg
          active:scale-95"
      >
        {downloading ? (
          <>
            <Loader2 className="w-4 h-4 animate-spin" />
            Downloading…
          </>
        ) : (
          <>
            <Download className="w-4 h-4" />
            Download PDF
          </>
        )}
      </button>

      {}
      {error && (
        <p className="text-sm text-red-500 dark:text-red-400 text-center">{error}</p>
      )}
    </div>
  );
};
