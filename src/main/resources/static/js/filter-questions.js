//filter list items
//source code from: https://www.w3schools.com/howto/howto_js_filter_lists.asp
function searchQuestion() {
  // Declare variables
  var input, filter, ul, li, a, i, txtValue;
  input = document.getElementById('inputSearch');
  filter = input.value.toUpperCase();
  ul = document.getElementById("filter-ul-list");
  li = ul.getElementsByTagName('li');

  // Loop through all list items, and hide those that don't match the search query
  for (i = 0; i < li.length; i++) {
    a = li[i].querySelector("#search-result-candidate");
    txtValue = a.textContent || a.innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      li[i].style.display = "";
    } else {
      li[i].style.display = "none";
    }
  }
}