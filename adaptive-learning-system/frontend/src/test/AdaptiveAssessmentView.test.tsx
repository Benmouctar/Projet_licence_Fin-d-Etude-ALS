










import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';


vi.mock('framer-motion', () => ({
  motion: {
    div: ({ children, ...props }: React.PropsWithChildren<React.HTMLAttributes<HTMLDivElement>>) =>
      React.createElement('div', props, children),
    span: ({ children, ...props }: React.PropsWithChildren<React.HTMLAttributes<HTMLSpanElement>>) =>
      React.createElement('span', props, children),
    button: ({ children, onClick, disabled, ...props }: React.PropsWithChildren<React.ButtonHTMLAttributes<HTMLButtonElement>>) =>
      React.createElement('button', { onClick, disabled, ...props }, children),
  },
  AnimatePresence: ({ children }: React.PropsWithChildren) => React.createElement(React.Fragment, null, children),
}));


vi.mock('../../services/adaptiveService', () => ({
  adaptiveService: {
    startSession: vi.fn(),
    submitAnswer: vi.fn(),
  },
}));

import { AdaptiveAssessmentView } from '../../views/learner/AdaptiveAssessmentView';
import { adaptiveService } from '../../services/adaptiveService';

/**
 * JavaScript/TypeScript utility module offering functions and structures for Adaptive Assessment View.test.
 */
const mockAdaptiveService = adaptiveService as {
  startSession: ReturnType<typeof vi.fn>;
  submitAnswer: ReturnType<typeof vi.fn>;
};



const mockQuestion = {
  id: 'q1',
  statement: 'What is a unit test?',
  options: ['A test covering the whole system', 'A test for one unit of code', 'A performance test', 'A UI test'],
  difficultyLevel: 'MEDIUM' as const,
};

const mockStartSession = {
  sessionId: 'sess-1',
  firstQuestion: mockQuestion,
  currentDifficulty: 'MEDIUM' as const,
  estimatedTotal: 10,
};

const defaultProps = {
  enrollmentId: 'enroll-1',
  moduleId: 'mod-1',
  moduleThreshold: 70,
  onComplete: vi.fn(),
  onBack: vi.fn(),
};



describe('AdaptiveAssessmentView', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockAdaptiveService.startSession.mockResolvedValue(mockStartSession);
  });

  
  it('displays first question after session starts', async () => {
    render(<AdaptiveAssessmentView {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('What is a unit test?')).toBeInTheDocument();
    });
  });

  
  it('calls submitAnswer when an option is clicked', async () => {
    const user = userEvent.setup();

    
    mockAdaptiveService.submitAnswer.mockResolvedValue({
      lastAnswerCorrect: true,
      questionsAnswered: 1,
      sessionComplete: false,
      newDifficulty: 'HARD' as const,
      nextQuestion: {
        id: 'q2',
        statement: 'What is mocking?',
        options: ['Real code', 'Test double', 'UI component', 'Database'],
        difficultyLevel: 'HARD' as const,
      },
      finalScore: null,
    });

    render(<AdaptiveAssessmentView {...defaultProps} />);

    
    await waitFor(() => {
      expect(screen.getByText('What is a unit test?')).toBeInTheDocument();
    });

    
    const options = screen.getAllByRole('button');
    
    const optionBtn = options.find(btn => btn.textContent?.includes('A test for one unit of code'));
    expect(optionBtn).toBeDefined();
    await user.click(optionBtn!);

    await waitFor(() => {
      expect(mockAdaptiveService.submitAnswer).toHaveBeenCalledWith(
        'sess-1',
        expect.objectContaining({ questionId: 'q1', selectedAnswer: 1 }),
      );
    });
  });

  
  it('shows final score when session is complete', async () => {
    const user = userEvent.setup();

    mockAdaptiveService.submitAnswer.mockResolvedValue({
      lastAnswerCorrect: true,
      questionsAnswered: 5,
      sessionComplete: true,
      newDifficulty: 'HARD' as const,
      nextQuestion: null,
      finalScore: 80,
    });

    render(<AdaptiveAssessmentView {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('What is a unit test?')).toBeInTheDocument();
    });

    const options = screen.getAllByRole('button');
    const optionBtn = options.find(btn => btn.textContent?.includes('A test for one unit of code'));
    await user.click(optionBtn!);

    await waitFor(() => {
      expect(screen.getByText('80%')).toBeInTheDocument();
    });
  });

  
  it('shows difficulty indicator with correct level highlighted', async () => {
    render(<AdaptiveAssessmentView {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('What is a unit test?')).toBeInTheDocument();
    });

    
    const mediumBadge = screen.getByText('Medium');
    expect(mediumBadge).toBeInTheDocument();
    
    expect(mediumBadge.className).toContain('bg-amber');
  });
});
