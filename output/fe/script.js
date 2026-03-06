// OpenClaw Documentation Site - Interactive Scripts

document.addEventListener('DOMContentLoaded', function() {
    // Mobile menu toggle
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navLinks = document.querySelector('.nav-links');
    
    if (mobileMenuBtn) {
        mobileMenuBtn.addEventListener('click', function() {
            navLinks.classList.toggle('mobile-open');
        });
    }

    // Copy button functionality
    const copyButtons = document.querySelectorAll('.copy-btn');
    
    copyButtons.forEach(btn => {
        btn.addEventListener('click', async function() {
            // Get code to copy from data-code attribute or sibling pre/code
            let codeToCopy = this.getAttribute('data-code');
            
            if (!codeToCopy) {
                const codeBlock = this.closest('.code-block');
                if (codeBlock) {
                    const code = codeBlock.querySelector('code');
                    if (code) {
                        codeToCopy = code.textContent;
                    }
                }
            }
            
            if (codeToCopy) {
                try {
                    await navigator.clipboard.writeText(codeToCopy.trim());
                    this.textContent = '已复制!';
                    this.classList.add('copied');
                    
                    setTimeout(() => {
                        this.textContent = this.getAttribute('data-code') ? '复制' : '复制';
                        this.classList.remove('copied');
                    }, 2000);
                } catch (err) {
                    console.error('Failed to copy:', err);
                    this.textContent = '复制失败';
                }
            }
        });
    });

    // Sidebar navigation - active state
    const sidebarLinks = document.querySelectorAll('.sidebar-link');
    const sections = document.querySelectorAll('.content-section');
    
    // Intersection Observer for scroll spy
    if ('IntersectionObserver' in window) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const id = entry.target.getAttribute('id');
                    if (id) {
                        sidebarLinks.forEach(link => {
                            link.classList.remove('active');
                            if (link.getAttribute('href') === `#${id}`) {
                                link.classList.add('active');
                            }
                        });
                    }
                }
            });
        }, {
            rootMargin: '-20% 0px -60% 0px'
        });
        
        sections.forEach(section => observer.observe(section));
    }

    // FAQ accordion
    const faqItems = document.querySelectorAll('.faq-item');
    
    faqItems.forEach(item => {
        const question = item.querySelector('.faq-question');
        if (question) {
            question.addEventListener('click', function() {
                // Close other items
                faqItems.forEach(otherItem => {
                    if (otherItem !== item && otherItem.classList.contains('active')) {
                        otherItem.classList.remove('active');
                    }
                });
                
                // Toggle current item
                item.classList.toggle('active');
            });
        }
    });

    // FAQ search functionality
    const faqSearch = document.getElementById('faqSearch');
    if (faqSearch) {
        faqSearch.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            
            faqItems.forEach(item => {
                const question = item.querySelector('.faq-question span');
                const answer = item.querySelector('.faq-answer-content');
                
                if (question && answer) {
                    const questionText = question.textContent.toLowerCase();
                    const answerText = answer.textContent.toLowerCase();
                    
                    if (questionText.includes(searchTerm) || answerText.includes(searchTerm)) {
                        item.style.display = '';
                    } else {
                        item.style.display = 'none';
                    }
                }
            });
        });
    }

    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href !== '#') {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    // Add copy button data-code from code blocks
    document.querySelectorAll('.code-block').forEach(block => {
        const btn = block.querySelector('.copy-btn');
        const code = block.querySelector('code');
        
        if (btn && code && !btn.getAttribute('data-code')) {
            // Get the first line of code as the copy command
            const firstLine = code.textContent.split('\n')[0].trim();
            btn.setAttribute('data-code', firstLine);
        }
    });

    // Update active nav link based on current page
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.nav-links a').forEach(link => {
        const linkPage = link.getAttribute('href');
        if (linkPage === currentPage) {
            link.classList.add('active');
        } else if (currentPage === '' || currentPage === '/') {
            // Homepage
            document.querySelector('.nav-links a[href="index.html"]')?.classList.add('active');
        }
    });
});
