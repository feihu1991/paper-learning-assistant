import React, { useState } from 'react';

const Home: React.FC = () => {
  const [query, setQuery] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Searching for:', query);
  };

  const papers = [
    {
      id: '1',
      title: 'Attention Is All You Need',
      authors: ['Vaswani, A.', 'Shazeer, N.', 'Parmar, N.', 'Uszkoreit, J.'],
      abstract: 'The dominant sequence transduction models are based on complex recurrent or convolutional neural networks...',
      source: 'arXiv',
      published_date: '2017-06-12'
    },
    {
      id: '2',
      title: 'BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding',
      authors: ['Devlin, J.', 'Chang, M.', 'Lee, K.', 'Toutanova, K.'],
      abstract: 'We introduce a new language representation model called BERT...',
      source: 'arXiv',
      published_date: '2018-10-11'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Paper Learning Assistant
          </h1>
          <p className="text-lg text-gray-600">
            Search, understand, and learn from academic papers with AI assistance
          </p>
        </div>

        <div className="max-w-3xl mx-auto mb-12">
          <form onSubmit={handleSearch} className="flex gap-2">
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search for papers (e.g., 'transformer models', 'machine learning')"
              className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <button
              type="submit"
              className="px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              Search
            </button>
          </form>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {papers.map((paper) => (
            <div key={paper.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
              <h3 className="text-xl font-semibold text-gray-900 mb-2">{paper.title}</h3>
              <p className="text-sm text-gray-600 mb-3">
                {paper.authors.join(', ')}
              </p>
              <p className="text-gray-700 mb-4 line-clamp-3">{paper.abstract}</p>
              <div className="flex justify-between items-center text-sm text-gray-500">
                <span>{paper.source}</span>
                <span>{paper.published_date}</span>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-12 text-center">
          <p className="text-gray-600">
            This is a demo of the Paper Learning Assistant website.
            <br />
            Full features including AI-powered explanations, visualizations, and user profiles are coming soon!
          </p>
        </div>
      </div>
    </div>
  );
};

export default Home;