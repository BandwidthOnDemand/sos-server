$(function() {
    var nocId = "noc-engineer"

    $("ul#users").on("click", "a.add", function(event) {
        event.preventDefault();

        var button = $(event.target).closest("a"),
            roleId = button.attr("data-roleid"),
            desc = button.attr("data-desc")
            userId = button.parent().parents("li").attr("data-nameid");

        $.ajax({
            data: JSON.stringify({title: roleId, description: desc, id: roleId}),
            type: 'POST',
            url: '/persons/'+userId+'/groups',
            contentType: "application/json; charset=utf-8",
            success: function() {
                $(button).addClass("remove").removeClass("add");
            },
            error: function(err) {
                $("#messages").append('<div>Failed to add group</div>');
            }
        });
    });

    $("ul#users").on("click", "a.remove", function(event) {
        event.preventDefault();

        var button = $(event.target).closest("a")
            roleId = button.attr("data-roleid");
            userId = button.parent().parents("li").attr("data-nameid");

        $.ajax({
            type: 'DELETE',
            url: '/persons/'+userId+'/groups/urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:'+roleId,
            success: function() {
                $(button).addClass("add").removeClass("remove");
            },
            error: function(err) {
                $("#messages").append('<div>Failed to remove group</div>');
            }
        });
    });
})