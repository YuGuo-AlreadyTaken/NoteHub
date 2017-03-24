package notehub

import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*
import grails.converters.*

/**
 * Controls the creation and retrieval of a User group (ie. Circle)
 * @author Yusra Al-Sharafi
 */
@Secured(['ROLE_USER'])
class UserGroupController extends RestfulController{
	static responseFormats = ['json']
    def springSecurityService

    UserGroupController(){
        super(UserGroup)
    }

    /**
     *  Default GET action for userGroup
     *  Accessed at /userGroup/
     *  @params id   Id of userGroup
     *  @return      Renders userGroup, 404, 401 or 400
     */
    def index() {
        //User group not found
        if(params.id == null){
            render(status:400)
            return
        }

        def userGroup
        try {
            userGroup = UserGroup.findById(Long.parseLong(params.id.toString()))
        } catch (NumberFormatException e){
            render(status:400)
            return
        }

        //Content not found
        if(userGroup == null){
            render(status:404)
            return
        }

        // Does not have access
        if (!userGroup.hasAccess(this.springSecurityService.currentUser)){
            render(status: 401)
            return
        }

        render(userGroup as JSON)
    }

    /**
     *  Default SAVE action for userGroup
     *  Accessed at /userGroup/
     *  Provide JSON with name and description
     *  @return      Renders 200, 404, or 400
     */
    def save(){
        //validate data
        if(request.JSON.name == null || request.JSON.description == null){
            render(status:400)
            return
        }

        String name
        String description

        // get JSON data
        try {
            name = request.JSON.name
            description = request.JSON.description
        } catch (NumberFormatException e) {
            render(status: 400)
            return
        }

        //find creator
        Account creatorAccount = this.springSecurityService.currentUser
        User creator = creatorAccount.getUser()

        def newUserGroup = new UserGroup(name, description, creator)

        //Ensuring validity of data
        if(!newUserGroup.validate()){
            render(status: 400)
            return
        }

        //Save creator user and new user group
        creator.save(flush:true)
        newUserGroup.save()
        render(status:200)
    }

    /**
     *  Default DELETE action for userGroup
     *  Accessed at /userGroup/id
     *  @param id    Id of userGroup
     *  @return      Renders 400, 404, 401 or 200
     */
    def delete(){
        //Id  doesn't exist
        if(params.id == null){
            render(status:400)
            return
        }

        def userGroup
        try {
            userGroup = UserGroup.findById(Long.parseLong(params.id.toString()))
        } catch (NumberFormatException e){
            render(status:400)
            return
        }

        if(userGroup == null){
            render(status:404)
            return
        }

        // Does not have access
        if (!userGroup.hasAccess(this.springSecurityService.currentUser)){
            render(status: 401)
            return
        }

        //Removes users from the UserGroup
        userGroup.getUsers().each {userGroup.removeFromUsers(it)}

        //Deletes User Group
        userGroup.delete(flush:true)
        render(status:200)
    }

    /**
     *  The GET action for userGroup
     *  Accessed at /userGroup/id
     *  @param id    Id of userGroup
     *  @return      Renders 400, 404, 401 or 200
     */
    def show(){
        //User group not found
        if(params.id == null){
            render(status:400)
            return
        }

        def userGroup
        try {
            userGroup = UserGroup.findById(Long.parseLong(params.id.toString()))
        } catch (NumberFormatException e){
            render(status:400)
            return
        }

        //Content not found
        if(userGroup == null){
            render(status:404)
            return
        }

        // Does not have access
        if (!userGroup.hasAccess(this.springSecurityService.currentUser)){
            render(status: 401)
            return
        }

        render(userGroup as JSON)
    }

}