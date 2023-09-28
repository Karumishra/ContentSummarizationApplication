document.addEventListener("DOMContentLoaded", function() {
    const fileInput = document.getElementById("fileInput");
    const processButton = document.getElementById("processButton");
    const resultDiv = document.getElementById("output");

    processButton.addEventListener("click", function() {
        const selectedFile = fileInput.files[0];

        if (selectedFile) {
            const formData = new FormData();
            formData.append("inputFile", selectedFile);

            // Show loading spinner and hide button
            processButton.style.display = "none";

            fetch("http://localhost:8081/api/summarizeInputFile", {
                method: "POST",
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                processButton.style.display = "block";
                // resultDiv.textContent = JSON.stringify(data["summary"], null, 2);
                resultDiv.textContent=data["summary"];
            })
            .catch(error => {
                console.error("Error:", error);
                resultDiv.textContent = "Internal Server Error!";
            });
        } else {
            resultDiv.textContent = "Select a file to Upload";
        }
    });
});
