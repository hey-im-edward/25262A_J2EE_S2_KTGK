(() => {
    const input = document.getElementById("course-search-input");
    const suggestions = document.getElementById("course-search-suggestions");

    if (!input || !suggestions) {
        return;
    }

    const suggestUrl = input.dataset.suggestUrl;
    if (!suggestUrl) {
        return;
    }

    let debounceTimer = null;
    let activeController = null;

    const hideSuggestions = () => {
        suggestions.innerHTML = "";
        suggestions.hidden = true;
    };

    const renderSuggestions = (courseNames) => {
        suggestions.innerHTML = "";

        if (!courseNames.length) {
            hideSuggestions();
            return;
        }

        courseNames.forEach((courseName) => {
            const link = document.createElement("a");
            link.className = "search-suggestion";
            link.href = `/courses?keyword=${encodeURIComponent(courseName)}`;
            link.textContent = courseName;
            suggestions.appendChild(link);
        });

        suggestions.hidden = false;
    };

    const loadSuggestions = async () => {
        const keyword = input.value.trim();
        if (!keyword) {
            hideSuggestions();
            return;
        }

        if (activeController) {
            activeController.abort();
        }

        activeController = new AbortController();

        try {
            const response = await fetch(`${suggestUrl}?keyword=${encodeURIComponent(keyword)}`, {
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                },
                signal: activeController.signal
            });

            if (!response.ok) {
                hideSuggestions();
                return;
            }

            const courseNames = await response.json();
            if (input.value.trim() !== keyword) {
                return;
            }

            renderSuggestions(Array.isArray(courseNames) ? courseNames : []);
        } catch (error) {
            if (error.name !== "AbortError") {
                hideSuggestions();
            }
        }
    };

    input.addEventListener("input", () => {
        window.clearTimeout(debounceTimer);
        debounceTimer = window.setTimeout(loadSuggestions, 180);
    });

    input.addEventListener("focus", () => {
        if (input.value.trim()) {
            loadSuggestions();
        }
    });

    input.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            hideSuggestions();
        }
    });

    document.addEventListener("click", (event) => {
        if (!suggestions.contains(event.target) && event.target !== input) {
            hideSuggestions();
        }
    });
})();
