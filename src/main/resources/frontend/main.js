var routes = {};
function route (path, templateId, controller) {
    routes[path] = {templateId: templateId, controller: controller};
}
