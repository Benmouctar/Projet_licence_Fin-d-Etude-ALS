











import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';




vi.mock('../../hooks/useChatHooks', () => ({
  useChatHistory: vi.fn(() => ({ data: undefined })),
  useSendMessage: vi.fn(() => ({ mutate: vi.fn() })),
}));


vi.mock('framer-motion', () => ({
  motion: {
    div: ({ children, ...props }: React.PropsWithChildren<React.HTMLAttributes<HTMLDivElement>>) =>
      React.createElement('div', props, children),
    span: ({ children, ...props }: React.PropsWithChildren<React.HTMLAttributes<HTMLSpanElement>>) =>
      React.createElement('span', props, children),
  },
  AnimatePresence: ({ children }: React.PropsWithChildren) => React.createElement(React.Fragment, null, children),
}));

import { AIChatUI } from '../../components/ui/AIChatUI';
import { useChatHistory, useSendMessage } from '../../hooks/useChatHooks';



/**
 * JavaScript/TypeScript utility module offering functions and structures for A I Chat U I.test.
 */
const mockUseChatHistory = useChatHistory as ReturnType<typeof vi.fn>;
const mockUseSendMessage = useSendMessage as ReturnType<typeof vi.fn>;

const defaultProps = {
  moduleId: 'mod-1',
  moduleTitle: 'Introduction to Testing',
  enrollmentId: 'enroll-1',
  onClose: vi.fn(),
};

const renderComponent = (props = defaultProps) => {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <AIChatUI {...props} />
    </QueryClientProvider>,
  );
};



describe('AIChatUI', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockUseChatHistory.mockReturnValue({ data: undefined });
    mockUseSendMessage.mockReturnValue({ mutate: vi.fn() });
  });

  
  it('renders empty state when no messages exist', () => {
    renderComponent();

    expect(screen.getByText(/Ask me anything about/i)).toBeInTheDocument();
    expect(screen.getByText(/Introduction to Testing/i)).toBeInTheDocument();
  });

  
  it('adds user message to the list after form submission', async () => {
    const user = userEvent.setup();

    
    let capturedOnSuccess: ((response: { response: string; isOutOfContext: boolean }) => void) | undefined;
    const mockMutate = vi.fn((_vars: unknown, options?: { onSuccess?: (r: { response: string; isOutOfContext: boolean }) => void }) => {
      capturedOnSuccess = options?.onSuccess;
    });
    mockUseSendMessage.mockReturnValue({ mutate: mockMutate });

    renderComponent();

    const textarea = screen.getByPlaceholderText(/Ask a question/i);
    await user.type(textarea, 'What is unit testing?');

    const sendBtn = screen.getByLabelText(/Send message/i);
    await user.click(sendBtn);

    
    await waitFor(() => {
      expect(screen.getByText('What is unit testing?')).toBeInTheDocument();
    });
  });

  
  
  it('shows typing indicator after message is sent', async () => {
    const user = userEvent.setup();

    
    const mockMutate = vi.fn();
    mockUseSendMessage.mockReturnValue({ mutate: mockMutate });

    renderComponent();

    const textarea = screen.getByPlaceholderText(/Ask a question/i);
    await user.type(textarea, 'Explain TDD');

    const sendBtn = screen.getByLabelText(/Send message/i);
    await user.click(sendBtn);

    
    await waitFor(() => {
      expect(screen.getByPlaceholderText(/Waiting for response/i)).toBeInTheDocument();
    });
  });

  
  it('renders AI message with "AI generated" badge', async () => {
    const aiMessage = {
      id: 'ai-1',
      role: 'ai' as const,
      content: 'Testing is the process of verifying software correctness.',
      isOutOfContext: false,
      timestamp: new Date(),
    };

    
    mockUseChatHistory.mockReturnValue({ data: [aiMessage] });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/AI generated/i)).toBeInTheDocument();
    });
  });

  
  it('out-of-context AI message uses amber styling class', async () => {
    const outOfContextMsg = {
      id: 'oc-1',
      role: 'ai' as const,
      content: 'I cannot answer questions outside the course scope.',
      isOutOfContext: true,
      timestamp: new Date(),
    };
    const regularAiMsg = {
      id: 'ai-2',
      role: 'ai' as const,
      content: 'Testing ensures code quality.',
      isOutOfContext: false,
      timestamp: new Date(),
    };

    mockUseChatHistory.mockReturnValue({ data: [outOfContextMsg, regularAiMsg] });

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText(/Outside course scope/i)).toBeInTheDocument();
    });
  });
});
